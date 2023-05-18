package com.fplService.databaseConnection;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fplService.databaseUtils.DatasourcePool;

public class DatabasePoolTest {

    static Connection connection;
    
    @Before
    public void build() {
        DatasourcePool.initiateDatabasePool();
    }

    @After
    public void CloseConnection() throws SQLException {
        DatasourcePool.closeConnectionPool();
    }

    @Test
    public void testDatabaseConnection() {

        connection = DatasourcePool.getDatabaseConnection();
        assertNotNull(connection);

    } 
    
}
