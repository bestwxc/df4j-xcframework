package com.df4j.xcframework.jdbc;

import org.springframework.lang.Nullable;
import java.sql.Connection;

@FunctionalInterface
public interface XcJdbcTemplateCallback {
    @Nullable
    void doInConnection(Connection con, XcJdbcTemplate xcJdbcTemplate);
}
