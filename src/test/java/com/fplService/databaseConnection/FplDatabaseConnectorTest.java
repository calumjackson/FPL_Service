package com.fplService.databaseConnection;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Test;

import com.fplService.databaseUtils.FplDatabaseConnector;

public class FplDatabaseConnectorTest {

    static Connection connection;
    
    @Test
    public void testDatabaseConnection() {

        try {
            connection = FplDatabaseConnector.getFplDbConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertNotNull(connection);

    } 

    @After
    public void CloseConnection() throws SQLException {
        FplDatabaseConnector.closeDatabaseConnection();
    }


    
}
