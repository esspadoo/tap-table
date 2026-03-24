package com.swad.taptable.util;

import java.sql.Connection;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author SWAD Team
 */
public class ConnectionPoolProvider {
    private static HikariDataSource dataSource;

    private ConnectionPoolProvider() {
        // private constructor to prevent instantiation
    }

    /**
     * Method to initialize the connection pool. Should be called once at app startup.
     * 
     * @param url the JDBC URL for the database
     * @param username the database username
     * @param password the database password
     */
    public static synchronized void init(String url, String username, String password) {
        /**
         * TODO: Evaluate if we want to pass these data through context params or through
         * .properties files
         */
        if (dataSource == null) {
            dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
        }
    }

    /**
     * Method to get a connection from the pool. Should be called whenever a DB connection is
     * needed.
     * 
     * @return a Connection object from the pool
     * @throws Exception if the pool is not initialized or if a connection cannot be obtained
     */
    public static Connection getConnection() throws Exception {
        if (dataSource == null) {
            throw new IllegalStateException("Connection pool not initialized");
        }
        return dataSource.getConnection();
    }

    /**
     * Method to close the connection pool. Should be called once at app shutdown to clean up
     * resources.
     */
    public static synchronized void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
