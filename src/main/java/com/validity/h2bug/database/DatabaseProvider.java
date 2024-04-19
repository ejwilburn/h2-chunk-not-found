/*
 * Copyright (c) 2024 Validity Inc.
 * All rights reserved.
 */

package com.validity.h2bug.database;

import com.validity.h2bug.H2ChunkNotFoundBug;
import com.validity.h2bug.exception.H2ChunkNotFoundBugException;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseProvider {

    INSTANCE;

    public static final String DB_CONNECTION_ERROR_MESSAGE = "Error connecting to database.";
    public static final String H2_JDBC_URL_PREFIX = "jdbc:h2:file:";
    public static final String DB_NAME = "h2bug";
    public static final String H2_OPTIONS = ";CACHE_SIZE=262144;WRITE_DELAY=5";

    private static final String DB_USERNAME = "h2";
    private static final String DB_PASSWORD = null;

    private Connection conn;

    public static DatabaseProvider getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() {
        return getConnection(buildJdbcUrl());
    }

    private Connection getConnection(String jdbcUrl) {
        try {
            if (conn == null || conn.isClosed()) {
                connect(jdbcUrl);
            }

            return conn;
        } catch (Exception ex) {
            throw new H2ChunkNotFoundBugException(DB_CONNECTION_ERROR_MESSAGE, ex);
        }
    }

    private void connect(String jdbcUrl) throws SQLException {
        conn = DriverManager.getConnection(jdbcUrl, DB_USERNAME, DB_PASSWORD);
    }

    private String buildJdbcUrl() {
        return H2_JDBC_URL_PREFIX + Path.of(H2ChunkNotFoundBug.H2_DB_DIR, DB_NAME) + H2_OPTIONS;
    }

}
