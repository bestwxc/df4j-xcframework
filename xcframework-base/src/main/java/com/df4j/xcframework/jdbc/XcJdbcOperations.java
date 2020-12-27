package com.df4j.xcframework.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 扩展JdbcOperations,用于支持传入Connection对象以使用事务
 */
public interface XcJdbcOperations extends JdbcOperations {

    <T> T execute(Connection con, ConnectionCallback<T> action) throws DataAccessException;


    <T> T execute(Connection con, StatementCallback<T> action) throws DataAccessException;


    void execute(Connection con, String sql) throws DataAccessException;


    <T> T query(Connection con, String sql, ResultSetExtractor<T> rse) throws DataAccessException;


    void query(Connection con, String sql, RowCallbackHandler rch) throws DataAccessException;


    <T> List<T> query(Connection con, String sql, RowMapper<T> rowMapper) throws DataAccessException;


    <T> T queryForObject(Connection con, String sql, RowMapper<T> rowMapper) throws DataAccessException;


    <T> T queryForObject(Connection con, String sql, Class<T> requiredType) throws DataAccessException;


    Map<String, Object> queryForMap(Connection con, String sql) throws DataAccessException;


    <T> List<T> queryForList(Connection con, String sql, Class<T> elementType) throws DataAccessException;


    List<Map<String, Object>> queryForList(Connection con, String sql) throws DataAccessException;


    SqlRowSet queryForRowSet(Connection con, String sql) throws DataAccessException;


    int update(Connection con, String sql) throws DataAccessException;


    int[] batchUpdate(Connection con, String... sql) throws DataAccessException;


    <T> T execute(Connection con, PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException;


    <T> T execute(Connection con, String sql, PreparedStatementCallback<T> action) throws DataAccessException;


    <T> T query(Connection con, PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException;


    <T> T query(Connection con, String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException;


    <T> T query(Connection con, String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException;


    <T> T query(Connection con, String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException;


    <T> T query(Connection con, String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException;


    void query(Connection con, PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException;


    void query(Connection con, String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException;


    void query(Connection con, String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException;


    void query(Connection con, String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException;


    void query(Connection con, String sql, RowCallbackHandler rch, Object... args) throws DataAccessException;


    <T> List<T> query(Connection con, PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException;


    <T> List<T> query(Connection con, String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException;


    <T> List<T> query(Connection con, String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException;


    <T> List<T> query(Connection con, String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException;


    <T> List<T> query(Connection con, String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;


    <T> T queryForObject(Connection con, String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException;


    <T> T queryForObject(Connection con, String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException;


    <T> T queryForObject(Connection con, String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;


    <T> T queryForObject(Connection con, String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException;


    <T> T queryForObject(Connection con, String sql, Object[] args, Class<T> requiredType) throws DataAccessException;


    <T> T queryForObject(Connection con, String sql, Class<T> requiredType, Object... args) throws DataAccessException;


    Map<String, Object> queryForMap(Connection con, String sql, Object[] args, int[] argTypes) throws DataAccessException;


    Map<String, Object> queryForMap(Connection con, String sql, Object... args) throws DataAccessException;


    <T> List<T> queryForList(Connection con, String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException;


    <T> List<T> queryForList(Connection con, String sql, Object[] args, Class<T> elementType) throws DataAccessException;


    <T> List<T> queryForList(Connection con, String sql, Class<T> elementType, Object... args) throws DataAccessException;


    List<Map<String, Object>> queryForList(Connection con, String sql, Object[] args, int[] argTypes) throws DataAccessException;


    List<Map<String, Object>> queryForList(Connection con, String sql, Object... args) throws DataAccessException;


    SqlRowSet queryForRowSet(Connection con, String sql, Object[] args, int[] argTypes) throws DataAccessException;


    SqlRowSet queryForRowSet(Connection con, String sql, Object... args) throws DataAccessException;


    int update(Connection con, PreparedStatementCreator psc) throws DataAccessException;


    int update(Connection con, PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException;


    int update(Connection con, String sql, PreparedStatementSetter pss) throws DataAccessException;


    int update(Connection con, String sql, Object[] args, int[] argTypes) throws DataAccessException;


    int update(Connection con, String sql, Object... args) throws DataAccessException;


    int[] batchUpdate(Connection con, String sql, BatchPreparedStatementSetter pss) throws DataAccessException;


    int[] batchUpdate(Connection con, String sql, List<Object[]> batchArgs) throws DataAccessException;


    int[] batchUpdate(Connection con, String sql, List<Object[]> batchArgs, int[] argTypes) throws DataAccessException;


    <T> int[][] batchUpdate(Connection con, String sql, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> pss) throws DataAccessException;


    <T> T execute(Connection con, CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException;


    <T> T execute(Connection con, String callString, CallableStatementCallback<T> action) throws DataAccessException;


    Map<String, Object> call(Connection con, CallableStatementCreator csc, List<SqlParameter> declaredParameters) throws DataAccessException;
}
