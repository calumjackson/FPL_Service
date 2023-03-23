package com.fplService.databaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class FplDatabaseConnector {

    Connection connection;

    public FplDatabaseConnector(Connection connection) {
        this.connection = connection;
    }

    public FplDatabaseConnector() throws SQLException {
        this.connection = connectToPostgresDatabase();
    }

    public Connection connectToPostgresDatabase() throws SQLException {

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/fplService", "calumjackson", "");
            // System.out.println("Connected to Database");

        } catch (SQLException e) {
            System.out.println("Not connected to Database");
            throw e;
        }

        return connection;

    }

    public void closeDatabaseConnection(Connection connection) throws SQLException {
        connection.close();
    }

    public Connection getFplDbConnection() {
        return connection;
    }

}
