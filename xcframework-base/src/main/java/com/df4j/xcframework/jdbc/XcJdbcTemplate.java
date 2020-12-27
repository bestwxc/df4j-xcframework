package com.df4j.xcframework.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 扩展JdbcTemplate,用于支持传入Connection对象以使用事务
 */
public class XcJdbcTemplate extends JdbcTemplate implements XcJdbcOperations {

    public XcJdbcTemplate() {
    }

    public XcJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    public XcJdbcTemplate(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }


    @Override
    public <T> T execute(Connection con, ConnectionCallback<T> action) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        Assert.notNull(action, "Callback object must not be null");
        try {
            Connection conToUse = createConnectionProxy(con);
            return action.doInConnection(conToUse);
        } catch (SQLException ex) {
            String sql = getSql(action);
            throw translateException("ConnectionCallback", sql, ex);
        }
    }

    @Override
    public <T> T execute(Connection con, StatementCallback<T> action) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        Assert.notNull(action, "Callback object must not be null");
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            applyStatementSettings(stmt);
            T result = action.doInStatement(stmt);
            handleWarnings(stmt);
            return result;
        } catch (SQLException ex) {
            String sql = getSql(action);
            JdbcUtils.closeStatement(stmt);
            stmt = null;
            throw translateException("StatementCallback", sql, ex);
        } finally {
            JdbcUtils.closeStatement(stmt);
        }
    }

    @Override
    public void execute(Connection con, String sql) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL statement [" + sql + "]");
        }

        class ExecuteStatementCallback implements StatementCallback<Object>, SqlProvider {
            @Override
            @Nullable
            public Object doInStatement(Statement stmt) throws SQLException {
                stmt.execute(sql);
                return null;
            }

            @Override
            public String getSql() {
                return sql;
            }
        }

        execute(new ExecuteStatementCallback());
    }

    @Override
    public <T> T query(Connection con, String sql, ResultSetExtractor<T> rse) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        Assert.notNull(sql, "SQL must not be null");
        Assert.notNull(rse, "ResultSetExtractor must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL query [" + sql + "]");
        }

        class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
            @Override
            @Nullable
            public T doInStatement(Statement stmt) throws SQLException {
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery(sql);
                    return rse.extractData(rs);
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }
            }

            @Override
            public String getSql() {
                return sql;
            }
        }

        return execute(new QueryStatementCallback());
    }

    @Override
    public void query(Connection con, String sql, RowCallbackHandler rch) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        query(con, sql, new RowCallbackHandlerResultSetExtractor(rch));
    }

    @Override
    public <T> List<T> query(Connection con, String sql, RowMapper<T> rowMapper) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(query(con, sql, new RowMapperResultSetExtractor<>(rowMapper)));
    }

    @Override
    public <T> T queryForObject(Connection con, String sql, RowMapper<T> rowMapper) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        List<T> results = query(con, sql, rowMapper);
        return DataAccessUtils.nullableSingleResult(results);
    }

    @Override
    public <T> T queryForObject(Connection con, String sql, Class<T> requiredType) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return queryForObject(con, sql, getSingleColumnRowMapper(requiredType));
    }

    @Override
    public Map<String, Object> queryForMap(Connection con, String sql) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(queryForObject(con, sql, getColumnMapRowMapper()));
    }

    @Override
    public <T> List<T> queryForList(Connection con, String sql, Class<T> elementType) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, getSingleColumnRowMapper(elementType));
    }

    @Override
    public List<Map<String, Object>> queryForList(Connection con, String sql) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, getColumnMapRowMapper());
    }

    @Override
    public SqlRowSet queryForRowSet(Connection con, String sql) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(query(con, sql, new SqlRowSetResultSetExtractor()));
    }

    @Override
    public int update(Connection con, String sql) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        Assert.notNull(sql, "SQL must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL update [" + sql + "]");
        }

        class UpdateStatementCallback implements StatementCallback<Integer>, SqlProvider {
            @Override
            public Integer doInStatement(Statement stmt) throws SQLException {
                int rows = stmt.executeUpdate(sql);
                if (logger.isTraceEnabled()) {
                    logger.trace("SQL update affected " + rows + " rows");
                }
                return rows;
            }

            @Override
            public String getSql() {
                return sql;
            }
        }

        return updateCount(execute(con, new UpdateStatementCallback()));
    }

    @Override
    public int[] batchUpdate(Connection con, String... sql) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        Assert.notEmpty(sql, "SQL array must not be empty");
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL batch update of " + sql.length + " statements");
        }

        class BatchUpdateStatementCallback implements StatementCallback<int[]>, SqlProvider {

            @Nullable
            private String currSql;

            @Override
            public int[] doInStatement(Statement stmt) throws SQLException, DataAccessException {
                int[] rowsAffected = new int[sql.length];
                if (JdbcUtils.supportsBatchUpdates(stmt.getConnection())) {
                    for (String sqlStmt : sql) {
                        this.currSql = appendSql(this.currSql, sqlStmt);
                        stmt.addBatch(sqlStmt);
                    }
                    try {
                        rowsAffected = stmt.executeBatch();
                    } catch (BatchUpdateException ex) {
                        String batchExceptionSql = null;
                        for (int i = 0; i < ex.getUpdateCounts().length; i++) {
                            if (ex.getUpdateCounts()[i] == Statement.EXECUTE_FAILED) {
                                batchExceptionSql = appendSql(batchExceptionSql, sql[i]);
                            }
                        }
                        if (StringUtils.hasLength(batchExceptionSql)) {
                            this.currSql = batchExceptionSql;
                        }
                        throw ex;
                    }
                } else {
                    for (int i = 0; i < sql.length; i++) {
                        this.currSql = sql[i];
                        if (!stmt.execute(sql[i])) {
                            rowsAffected[i] = stmt.getUpdateCount();
                        } else {
                            throw new InvalidDataAccessApiUsageException("Invalid batch SQL statement: " + sql[i]);
                        }
                    }
                }
                return rowsAffected;
            }

            private String appendSql(@Nullable String sql, String statement) {
                return (StringUtils.hasLength(sql) ? sql + "; " + statement : statement);
            }

            @Override
            @Nullable
            public String getSql() {
                return this.currSql;
            }
        }

        int[] result = execute(con, new BatchUpdateStatementCallback());
        Assert.state(result != null, "No update counts");
        return result;
    }

    @Override
    public <T> T execute(Connection con, PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        Assert.notNull(psc, "PreparedStatementCreator must not be null");
        Assert.notNull(action, "Callback object must not be null");
        if (logger.isDebugEnabled()) {
            String sql = getSql(psc);
            logger.debug("Executing prepared SQL statement" + (sql != null ? " [" + sql + "]" : ""));
        }

        PreparedStatement ps = null;
        try {
            ps = psc.createPreparedStatement(con);
            applyStatementSettings(ps);
            T result = action.doInPreparedStatement(ps);
            handleWarnings(ps);
            return result;
        } catch (SQLException ex) {
            // Release Connection early, to avoid potential connection pool deadlock
            // in the case when the exception translator hasn't been initialized yet.
            if (psc instanceof ParameterDisposer) {
                ((ParameterDisposer) psc).cleanupParameters();
            }
            String sql = getSql(psc);
            psc = null;
            JdbcUtils.closeStatement(ps);
            ps = null;
            throw translateException("PreparedStatementCallback", sql, ex);
        } finally {
            if (psc instanceof ParameterDisposer) {
                ((ParameterDisposer) psc).cleanupParameters();
            }
            JdbcUtils.closeStatement(ps);
        }
    }

    @Override
    public <T> T execute(Connection con, String sql, PreparedStatementCallback<T> action) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return execute(con, new SimplePreparedStatementCreator(sql), action);
    }

    @Nullable
    public <T> T query(
            Connection con,
            PreparedStatementCreator psc, @Nullable final PreparedStatementSetter pss, final ResultSetExtractor<T> rse)
            throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        Assert.notNull(rse, "ResultSetExtractor must not be null");
        logger.debug("Executing prepared SQL query");

        return execute(con, psc, new PreparedStatementCallback<T>() {
            @Override
            @Nullable
            public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
                ResultSet rs = null;
                try {
                    if (pss != null) {
                        pss.setValues(ps);
                    }
                    rs = ps.executeQuery();
                    return rse.extractData(rs);
                } finally {
                    JdbcUtils.closeResultSet(rs);
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer) pss).cleanupParameters();
                    }
                }
            }
        });
    }

    @Override
    public <T> T query(Connection con, PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, psc, null, rse);
    }

    @Override
    public <T> T query(Connection con, String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, new SimplePreparedStatementCreator(sql), pss, rse);
    }

    @Override
    public <T> T query(Connection con, String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, newArgTypePreparedStatementSetter(args, argTypes), rse);
    }

    @Override
    public <T> T query(Connection con, String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, newArgPreparedStatementSetter(args), rse);
    }

    @Override
    public <T> T query(Connection con, String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, newArgPreparedStatementSetter(args), rse);
    }

    @Override
    public void query(Connection con, PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        query(con, psc, new RowCallbackHandlerResultSetExtractor(rch));
    }

    @Override
    public void query(Connection con, String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        query(con, sql, pss, new RowCallbackHandlerResultSetExtractor(rch));
    }

    @Override
    public void query(Connection con, String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        query(con, sql, newArgTypePreparedStatementSetter(args, argTypes), rch);
    }

    @Override
    public void query(Connection con, String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        query(con, sql, newArgPreparedStatementSetter(args), rch);
    }

    @Override
    public void query(Connection con, String sql, RowCallbackHandler rch, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        query(con, sql, newArgPreparedStatementSetter(args), rch);
    }

    @Override
    public <T> List<T> query(Connection con, PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(query(con, psc, new RowMapperResultSetExtractor<>(rowMapper)));
    }

    @Override
    public <T> List<T> query(Connection con, String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(query(con, sql, pss, new RowMapperResultSetExtractor<>(rowMapper)));
    }

    @Override
    public <T> List<T> query(Connection con, String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(query(con, sql, args, argTypes, new RowMapperResultSetExtractor<>(rowMapper)));

    }

    @Override
    public <T> List<T> query(Connection con, String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(query(con, sql, args, new RowMapperResultSetExtractor<>(rowMapper)));

    }

    @Override
    public <T> List<T> query(Connection con, String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(query(con, sql, args, new RowMapperResultSetExtractor<>(rowMapper)));

    }

    @Override
    public <T> T queryForObject(Connection con, String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        List<T> results = query(con, sql, args, argTypes, new RowMapperResultSetExtractor<>(rowMapper, 1));
        return DataAccessUtils.nullableSingleResult(results);
    }

    @Override
    public <T> T queryForObject(Connection con, String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        List<T> results = query(con, sql, args, new RowMapperResultSetExtractor<>(rowMapper, 1));
        return DataAccessUtils.nullableSingleResult(results);
    }

    @Override
    public <T> T queryForObject(Connection con, String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        List<T> results = query(con, sql, args, new RowMapperResultSetExtractor<>(rowMapper, 1));
        return DataAccessUtils.nullableSingleResult(results);
    }

    @Override
    public <T> T queryForObject(Connection con, String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return queryForObject(con, sql, args, argTypes, getSingleColumnRowMapper(requiredType));

    }

    @Override
    public <T> T queryForObject(Connection con, String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return queryForObject(con, sql, args, getSingleColumnRowMapper(requiredType));

    }

    @Override
    public <T> T queryForObject(Connection con, String sql, Class<T> requiredType, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return queryForObject(con, sql, args, getSingleColumnRowMapper(requiredType));

    }

    @Override
    public Map<String, Object> queryForMap(Connection con, String sql, Object[] args, int[] argTypes) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(queryForObject(con, sql, args, argTypes, getColumnMapRowMapper()));

    }

    @Override
    public Map<String, Object> queryForMap(Connection con, String sql, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(queryForObject(con, sql, args, getColumnMapRowMapper()));

    }

    @Override
    public <T> List<T> queryForList(Connection con, String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, args, argTypes, getSingleColumnRowMapper(elementType));

    }

    @Override
    public <T> List<T> queryForList(Connection con, String sql, Object[] args, Class<T> elementType) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, args, getSingleColumnRowMapper(elementType));

    }

    @Override
    public <T> List<T> queryForList(Connection con, String sql, Class<T> elementType, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, args, getSingleColumnRowMapper(elementType));

    }

    @Override
    public List<Map<String, Object>> queryForList(Connection con, String sql, Object[] args, int[] argTypes) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, args, argTypes, getColumnMapRowMapper());

    }

    @Override
    public List<Map<String, Object>> queryForList(Connection con, String sql, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return query(con, sql, args, getColumnMapRowMapper());

    }

    @Override
    public SqlRowSet queryForRowSet(Connection con, String sql, Object[] args, int[] argTypes) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(query(con, sql, args, argTypes, new SqlRowSetResultSetExtractor()));

    }

    @Override
    public SqlRowSet queryForRowSet(Connection con, String sql, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return result(query(con, sql, args, new SqlRowSetResultSetExtractor()));

    }

    protected int update(Connection con, final PreparedStatementCreator psc, @Nullable final PreparedStatementSetter pss)
            throws DataAccessException {

        logger.debug("Executing prepared SQL update");

        return updateCount(execute(con, psc, ps -> {
            try {
                if (pss != null) {
                    pss.setValues(ps);
                }
                int rows = ps.executeUpdate();
                if (logger.isTraceEnabled()) {
                    logger.trace("SQL update affected " + rows + " rows");
                }
                return rows;
            } finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer) pss).cleanupParameters();
                }
            }
        }));
    }

    @Override
    public int update(Connection con, PreparedStatementCreator psc) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return update(con, psc, (PreparedStatementSetter) null);
    }

    @Override
    public int update(Connection con, PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        Assert.notNull(generatedKeyHolder, "KeyHolder must not be null");
        logger.debug("Executing SQL update and returning generated keys");

        return updateCount(execute(con, psc, ps -> {
            int rows = ps.executeUpdate();
            List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
            generatedKeys.clear();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys != null) {
                try {
                    RowMapperResultSetExtractor<Map<String, Object>> rse =
                            new RowMapperResultSetExtractor<>(getColumnMapRowMapper(), 1);
                    generatedKeys.addAll(result(rse.extractData(keys)));
                } finally {
                    JdbcUtils.closeResultSet(keys);
                }
            }
            if (logger.isTraceEnabled()) {
                logger.trace("SQL update affected " + rows + " rows and returned " + generatedKeys.size() + " keys");
            }
            return rows;
        }));
    }

    @Override
    public int update(Connection con, String sql, PreparedStatementSetter pss) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return update(con, new SimplePreparedStatementCreator(sql), pss);
    }

    @Override
    public int update(Connection con, String sql, Object[] args, int[] argTypes) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return update(con, sql, newArgTypePreparedStatementSetter(args, argTypes));
    }

    @Override
    public int update(Connection con, String sql, Object... args) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return update(con, sql, newArgPreparedStatementSetter(args));
    }

    @Override
    public int[] batchUpdate(Connection con, String sql, BatchPreparedStatementSetter pss) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL batch update [" + sql + "]");
        }

        int[] result = execute(con, sql, (PreparedStatementCallback<int[]>) ps -> {
            try {
                int batchSize = pss.getBatchSize();
                InterruptibleBatchPreparedStatementSetter ipss =
                        (pss instanceof InterruptibleBatchPreparedStatementSetter ?
                                (InterruptibleBatchPreparedStatementSetter) pss : null);
                if (JdbcUtils.supportsBatchUpdates(ps.getConnection())) {
                    for (int i = 0; i < batchSize; i++) {
                        pss.setValues(ps, i);
                        if (ipss != null && ipss.isBatchExhausted(i)) {
                            break;
                        }
                        ps.addBatch();
                    }
                    return ps.executeBatch();
                } else {
                    List<Integer> rowsAffected = new ArrayList<>();
                    for (int i = 0; i < batchSize; i++) {
                        pss.setValues(ps, i);
                        if (ipss != null && ipss.isBatchExhausted(i)) {
                            break;
                        }
                        rowsAffected.add(ps.executeUpdate());
                    }
                    int[] rowsAffectedArray = new int[rowsAffected.size()];
                    for (int i = 0; i < rowsAffectedArray.length; i++) {
                        rowsAffectedArray[i] = rowsAffected.get(i);
                    }
                    return rowsAffectedArray;
                }
            } finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer) pss).cleanupParameters();
                }
            }
        });

        Assert.state(result != null, "No result array");
        return result;
    }

    @Override
    public int[] batchUpdate(Connection con, String sql, List<Object[]> batchArgs) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return batchUpdate(con, sql, batchArgs, new int[0]);
    }

    @Override
    public int[] batchUpdate(Connection con, String sql, List<Object[]> batchArgs, int[] argTypes) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        if (batchArgs.isEmpty()) {
            return new int[0];
        }

        return batchUpdate(con,
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Object[] values = batchArgs.get(i);
                        int colIndex = 0;
                        for (Object value : values) {
                            colIndex++;
                            if (value instanceof SqlParameterValue) {
                                SqlParameterValue paramValue = (SqlParameterValue) value;
                                StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
                            } else {
                                int colType;
                                if (argTypes.length < colIndex) {
                                    colType = SqlTypeValue.TYPE_UNKNOWN;
                                } else {
                                    colType = argTypes[colIndex - 1];
                                }
                                StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
                            }
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return batchArgs.size();
                    }
                });
    }

    @Override
    public <T> int[][] batchUpdate(Connection con, String sql, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> pss) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL batch update [" + sql + "] with a batch size of " + batchSize);
        }
        int[][] result = execute(con, sql, (PreparedStatementCallback<int[][]>) ps -> {
            List<int[]> rowsAffected = new ArrayList<>();
            try {
                boolean batchSupported = JdbcUtils.supportsBatchUpdates(ps.getConnection());
                int n = 0;
                for (T obj : batchArgs) {
                    pss.setValues(ps, obj);
                    n++;
                    if (batchSupported) {
                        ps.addBatch();
                        if (n % batchSize == 0 || n == batchArgs.size()) {
                            if (logger.isTraceEnabled()) {
                                int batchIdx = (n % batchSize == 0) ? n / batchSize : (n / batchSize) + 1;
                                int items = n - ((n % batchSize == 0) ? n / batchSize - 1 : (n / batchSize)) * batchSize;
                                logger.trace("Sending SQL batch update #" + batchIdx + " with " + items + " items");
                            }
                            rowsAffected.add(ps.executeBatch());
                        }
                    } else {
                        int i = ps.executeUpdate();
                        rowsAffected.add(new int[]{i});
                    }
                }
                int[][] result1 = new int[rowsAffected.size()][];
                for (int i = 0; i < result1.length; i++) {
                    result1[i] = rowsAffected.get(i);
                }
                return result1;
            } finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer) pss).cleanupParameters();
                }
            }
        });

        Assert.state(result != null, "No result array");
        return result;
    }

    @Override
    public <T> T execute(Connection con, CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        Assert.notNull(csc, "CallableStatementCreator must not be null");
        Assert.notNull(action, "Callback object must not be null");
        if (logger.isDebugEnabled()) {
            String sql = getSql(csc);
            logger.debug("Calling stored procedure" + (sql != null ? " [" + sql + "]" : ""));
        }
        CallableStatement cs = null;
        try {
            cs = csc.createCallableStatement(con);
            applyStatementSettings(cs);
            T result = action.doInCallableStatement(cs);
            handleWarnings(cs);
            return result;
        } catch (SQLException ex) {
            if (csc instanceof ParameterDisposer) {
                ((ParameterDisposer) csc).cleanupParameters();
            }
            String sql = getSql(csc);
            csc = null;
            JdbcUtils.closeStatement(cs);
            cs = null;
            throw translateException("CallableStatementCallback", sql, ex);
        } finally {
            if (csc instanceof ParameterDisposer) {
                ((ParameterDisposer) csc).cleanupParameters();
            }
            JdbcUtils.closeStatement(cs);
        }
    }

    @Override
    public <T> T execute(Connection con, String callString, CallableStatementCallback<T> action) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        return execute(con, new SimpleCallableStatementCreator(callString), action);
    }

    @Override
    public Map<String, Object> call(Connection con, CallableStatementCreator csc, List<SqlParameter> declaredParameters) throws DataAccessException {
        Assert.notNull(con, "con object must not be null");
        final List<SqlParameter> updateCountParameters = new ArrayList<>();
        final List<SqlParameter> resultSetParameters = new ArrayList<>();
        final List<SqlParameter> callParameters = new ArrayList<>();

        for (SqlParameter parameter : declaredParameters) {
            if (parameter.isResultsParameter()) {
                if (parameter instanceof SqlReturnResultSet) {
                    resultSetParameters.add(parameter);
                } else {
                    updateCountParameters.add(parameter);
                }
            } else {
                callParameters.add(parameter);
            }
        }

        Map<String, Object> result = execute(con, csc, cs -> {
            boolean retVal = cs.execute();
            int updateCount = cs.getUpdateCount();
            if (logger.isTraceEnabled()) {
                logger.trace("CallableStatement.execute() returned '" + retVal + "'");
                logger.trace("CallableStatement.getUpdateCount() returned " + updateCount);
            }
            Map<String, Object> resultsMap = createResultsMap();
            if (retVal || updateCount != -1) {
                resultsMap.putAll(extractReturnedResults(cs, updateCountParameters, resultSetParameters, updateCount));
            }
            resultsMap.putAll(extractOutputParameters(cs, callParameters));
            return resultsMap;
        });

        Assert.state(result != null, "No result map");
        return result;
    }

    @Nullable
    private static String getSql(Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider) {
            return ((SqlProvider) sqlProvider).getSql();
        } else {
            return null;
        }
    }

    private static <T> T result(@Nullable T result) {
        Assert.state(result != null, "No result");
        return result;
    }

    private static int updateCount(@Nullable Integer result) {
        Assert.state(result != null, "No update count");
        return result;
    }


    private class CloseSuppressingInvocationHandler implements InvocationHandler {

        private final Connection target;

        public CloseSuppressingInvocationHandler(Connection target) {
            this.target = target;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Invocation on ConnectionProxy interface coming in...

            if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
            } else if (method.getName().equals("hashCode")) {
                // Use hashCode of PersistenceManager proxy.
                return System.identityHashCode(proxy);
            } else if (method.getName().equals("unwrap")) {
                if (((Class<?>) args[0]).isInstance(proxy)) {
                    return proxy;
                }
            } else if (method.getName().equals("isWrapperFor")) {
                if (((Class<?>) args[0]).isInstance(proxy)) {
                    return true;
                }
            } else if (method.getName().equals("close")) {
                // Handle close method: suppress, not valid.
                return null;
            } else if (method.getName().equals("isClosed")) {
                return false;
            } else if (method.getName().equals("getTargetConnection")) {
                // Handle getTargetConnection method: return underlying Connection.
                return this.target;
            }

            // Invoke method on target Connection.
            try {
                Object retVal = method.invoke(this.target, args);

                // If return value is a JDBC Statement, apply statement settings
                // (fetch size, max rows, transaction timeout).
                if (retVal instanceof Statement) {
                    applyStatementSettings(((Statement) retVal));
                }

                return retVal;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

        private final String sql;

        public SimplePreparedStatementCreator(String sql) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql);
        }

        @Override
        public String getSql() {
            return this.sql;
        }
    }

    private static class SimpleCallableStatementCreator implements CallableStatementCreator, SqlProvider {

        private final String callString;

        public SimpleCallableStatementCreator(String callString) {
            Assert.notNull(callString, "Call string must not be null");
            this.callString = callString;
        }

        @Override
        public CallableStatement createCallableStatement(Connection con) throws SQLException {
            return con.prepareCall(this.callString);
        }

        @Override
        public String getSql() {
            return this.callString;
        }
    }


    private static class RowCallbackHandlerResultSetExtractor implements ResultSetExtractor<Object> {

        private final RowCallbackHandler rch;

        public RowCallbackHandlerResultSetExtractor(RowCallbackHandler rch) {
            this.rch = rch;
        }

        @Override
        @Nullable
        public Object extractData(ResultSet rs) throws SQLException {
            while (rs.next()) {
                this.rch.processRow(rs);
            }
            return null;
        }
    }
}
