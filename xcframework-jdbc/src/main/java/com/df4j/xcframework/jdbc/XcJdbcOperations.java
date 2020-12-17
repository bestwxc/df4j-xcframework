package com.df4j.xcframework.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface XcJdbcOperations extends JdbcOperations {

    <T> T execute(Connection connection, ConnectionCallback<T> action) throws DataAccessException;


    <T> T execute(Connection connection, StatementCallback<T> action) throws DataAccessException;


    void execute(Connection connection, String sql) throws DataAccessException;


    <T> T query(Connection connection, String sql, ResultSetExtractor<T> rse) throws DataAccessException;


    void query(Connection connection, String sql, RowCallbackHandler rch) throws DataAccessException;


    <T> List<T> query(Connection connection, String sql, RowMapper<T> rowMapper) throws DataAccessException;


    <T> T queryForObject(Connection connection, String sql, RowMapper<T> rowMapper) throws DataAccessException;


    <T> T queryForObject(Connection connection, String sql, Class<T> requiredType) throws DataAccessException;


    Map<String, Object> queryForMap(Connection connection, String sql) throws DataAccessException;


    <T> List<T> queryForList(Connection connection, String sql, Class<T> elementType) throws DataAccessException;


    List<Map<String, Object>> queryForList(Connection connection, String sql) throws DataAccessException;


    SqlRowSet queryForRowSet(Connection connection, String sql) throws DataAccessException;


    int update(Connection connection, String sql) throws DataAccessException;


    int[] batchUpdate(Connection connection, String... sql) throws DataAccessException;


    <T> T execute(Connection connection, PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException;


    <T> T execute(Connection connection, String sql, PreparedStatementCallback<T> action) throws DataAccessException;


    <T> T query(Connection connection, PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException;


    <T> T query(Connection connection, String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException;


    <T> T query(Connection connection, String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException;


    <T> T query(Connection connection, String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException;


    <T> T query(Connection connection, String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException;


    void query(Connection connection, PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException;


    void query(Connection connection, String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException;


    void query(Connection connection, String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException;


    void query(Connection connection, String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException;


    void query(Connection connection, String sql, RowCallbackHandler rch, Object... args) throws DataAccessException;


    <T> List<T> query(Connection connection, PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException;


    <T> List<T> query(Connection connection, String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException;


    <T> List<T> query(Connection connection, String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException;


    <T> List<T> query(Connection connection, String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException;


    <T> List<T> query(Connection connection, String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;


    <T> T queryForObject(Connection connection, String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException;


    <T> T queryForObject(Connection connection, String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException;


    <T> T queryForObject(Connection connection, String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;


    <T> T queryForObject(Connection connection, String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException;


    <T> T queryForObject(Connection connection, String sql, Object[] args, Class<T> requiredType) throws DataAccessException;


    <T> T queryForObject(Connection connection, String sql, Class<T> requiredType, Object... args) throws DataAccessException;


    Map<String, Object> queryForMap(Connection connection, String sql, Object[] args, int[] argTypes) throws DataAccessException;


    Map<String, Object> queryForMap(Connection connection, String sql, Object... args) throws DataAccessException;


    <T> List<T> queryForList(Connection connection, String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException;


    <T> List<T> queryForList(Connection connection, String sql, Object[] args, Class<T> elementType) throws DataAccessException;


    <T> List<T> queryForList(Connection connection, String sql, Class<T> elementType, Object... args) throws DataAccessException;


    List<Map<String, Object>> queryForList(Connection connection, String sql, Object[] args, int[] argTypes) throws DataAccessException;


    List<Map<String, Object>> queryForList(Connection connection, String sql, Object... args) throws DataAccessException;


    SqlRowSet queryForRowSet(Connection connection, String sql, Object[] args, int[] argTypes) throws DataAccessException;


    SqlRowSet queryForRowSet(Connection connection, String sql, Object... args) throws DataAccessException;


    int update(Connection connection, PreparedStatementCreator psc) throws DataAccessException;


    int update(Connection connection, PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException;


    int update(Connection connection, String sql, PreparedStatementSetter pss) throws DataAccessException;


    int update(Connection connection, String sql, Object[] args, int[] argTypes) throws DataAccessException;


    int update(Connection connection, String sql, Object... args) throws DataAccessException;


    int[] batchUpdate(Connection connection, String sql, BatchPreparedStatementSetter pss) throws DataAccessException;


    int[] batchUpdate(Connection connection, String sql, List<Object[]> batchArgs) throws DataAccessException;


    int[] batchUpdate(Connection connection, String sql, List<Object[]> batchArgs, int[] argTypes) throws DataAccessException;


    <T> int[][] batchUpdate(Connection connection, String sql, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> pss) throws DataAccessException;


    <T> T execute(Connection connection, CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException;


    <T> T execute(Connection connection, String callString, CallableStatementCallback<T> action) throws DataAccessException;


    Map<String, Object> call(Connection connection, CallableStatementCreator csc, List<SqlParameter> declaredParameters) throws DataAccessException;
}
