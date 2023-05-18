package com.fplService.databaseUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FplDatabaseConnector {

    private static Connection connection;

    public FplDatabaseConnector() throws SQLException {
        
        FplDatabaseConnector.connection = connectToPostgresDatabase();
    }

    private static Connection connectToPostgresDatabase() throws SQLException {
        Logger logger = LoggerFactory.getLogger(FplDatabaseConnector.class);
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/calumjackson", "calumjackson", "postgres");
            logger.info("Connected to database");
        } catch (SQLException e) {
            System.out.println("Error: Not connected to Database");
            throw e;
        }

        return connection;

    }

    public static void closeDatabaseConnection() throws SQLException {
        connection.close();
    }

    public static Connection getFplDbConnection() throws SQLException {
        if (connection == null) {
            connection = connectToPostgresDatabase();
        } else if (connection.isClosed()) {
            connection = connectToPostgresDatabase();
        }
        return connection;
    }

}
