package com.fplService.databaseConnection;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

public class FplDatabaseConnectorTest {

    
    @Test
    public void testDatabaseConnection() {

        Connection connection = null;
        try {
            connection = FplDatabaseConnector.getFplDbConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertNotNull(connection);

    } 


    
}
