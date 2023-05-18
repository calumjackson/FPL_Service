package com.fplService.databaseConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseUtils.DatasourcePool;

public class DatabasePoolTest {

    static Connection connection;
    Logger logger;
    
    @Before
    public void setup() {
        logger = LoggerFactory.getLogger(DatabasePoolTest.class);
    }

    @After
    public void CloseConnection() throws SQLException {
        DatasourcePool.closeConnectionPool();
    }

    @Test
    public void testDatabaseConnection() throws SQLException {
        DatasourcePool.initiateDatabasePool();
        connection = DatasourcePool.getDatabaseConnection();
        System.out.println(connection.isClosed());
        assertNotNull(connection);
    } 

    @Test
    public void testMultiConnection() throws SQLException {
        DatasourcePool.initiateDatabasePool();
        connection = DatasourcePool.getDatabaseConnection();
        assertNotNull(connection);

        Connection secondConnection = DatasourcePool.getDatabaseConnection();
        assertNotEquals(connection, secondConnection);

        connection.close();
        assertTrue(connection.isClosed());
        assertFalse(secondConnection.isClosed());
        secondConnection.close();
        assertTrue(secondConnection.isClosed());
    }

    @Test
    public void testAvailability() throws SQLException {

        DatasourcePool.initiateDatabasePool();
        connection = DatasourcePool.getDatabaseConnection();
        connection = DatasourcePool.getDatabaseConnection();
        Connection connectionThree = DatasourcePool.getDatabaseConnection();
        assertNotNull(connection);
        logger.info("Active connections: " + DatasourcePool.getActiveConnections());
        logger.info("Idle connections: " + DatasourcePool.getIdleConnections());
        logger.info("Awaiting connections: " + DatasourcePool.getIdleConnections());
        
        connection.close();
        logger.info("Active connections: " + DatasourcePool.getActiveConnections());
        connection.close();
        logger.info("Active connections: " + DatasourcePool.getActiveConnections());
        connectionThree.close();
        logger.info("Active connections: " + DatasourcePool.getActiveConnections());

        
        DatasourcePool.closeConnectionPool();
        logger.info("Final Active connections: " + DatasourcePool.getActiveConnections());
        Integer expected = 0;
        assertEquals(expected, DatasourcePool.getActiveConnections());
    } 
    
    
}
