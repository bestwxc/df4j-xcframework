package com.df4j.xcframework.jdbc;

import com.df4j.xcframework.base.exception.XcException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcUtils {

    public static void doInConnection(XcJdbcTemplate xcJdbcTemplate, XcJdbcTemplateCallback callback) {
        Assert.notNull(xcJdbcTemplate, "xcJdbcTemplate object must not null!");
        Assert.notNull(callback, "XcJdbcTemplateCallback object must not null!");
        Connection con = null;
        try {
            con = DataSourceUtils.getConnection(xcJdbcTemplate.getDataSource());
            setAutoCommit(con, false);
            callback.doInConnection(con, xcJdbcTemplate);
            commit(con);
        } catch (XcException e) {
            rollback(con);
            throw e;
        } finally {
            setAutoCommit(con, true);
            DataSourceUtils.releaseConnection(con, xcJdbcTemplate.getDataSource());
        }
    }

    public static void commit(Connection con) {
        try {
            con.commit();
        } catch (SQLException e) {
            throw new XcException(e);
        }
    }

    public static void rollback(Connection con) {
        try {
            con.rollback();
        } catch (SQLException e) {
            throw new XcException(e);
        }
    }

    public static void setAutoCommit(Connection con, boolean autoCommit) {
        Assert.notNull(con, "con object must not be null!");
        try {
            con.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new XcException(e);
        }
    }
}
