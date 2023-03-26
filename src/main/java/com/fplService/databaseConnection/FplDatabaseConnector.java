package com.fplService.databaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class FplDatabaseConnector {

    private static Connection connection;

    private FplDatabaseConnector() throws SQLException {
        
        FplDatabaseConnector.connection = connectToPostgresDatabase();
    }

    private static Connection connectToPostgresDatabase() throws SQLException {

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/fplService", "calumjackson", "");
            // System.out.println("Connected to Database");

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
