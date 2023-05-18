package com.fplService.databaseUtils;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatasourcePool {

    private static HikariDataSource dataSource;
    static Logger logger;

	public static void initiateDatabasePool() {
        logger = LoggerFactory.getLogger(DatasourcePool.class);

        HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:postgresql://localhost:5432/calumjackson");
		config.setUsername("calumjackson");
		config.setPassword("postgres");
		config.addDataSourceProperty("minimumIdle", "5");
		config.addDataSourceProperty("maximumPoolSize", "5");

		dataSource = new HikariDataSource(config);
	}

    public static Connection getDatabaseConnection() {
        logger = LoggerFactory.getLogger(DatasourcePool.class);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            if (connection.isClosed()) {
                initiateDatabasePool();
                return dataSource.getConnection();
            }
        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
        return connection;
    }

    public static void closeConnectionPool() {
        dataSource.close();
    }
    
}
