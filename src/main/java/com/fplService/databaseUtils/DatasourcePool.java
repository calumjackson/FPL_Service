package com.fplService.databaseUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

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

    public static Connection getDatabaseConnection() throws SQLException {
        logger = LoggerFactory.getLogger(DatasourcePool.class);
        try {
            if (!Objects.isNull(dataSource)) {
                initiateDatabasePool();
                return dataSource.getConnection();
            }

            if (dataSource.isClosed()) {
                    initiateDatabasePool();
                    return dataSource.getConnection();
                }
        } catch (SQLException e) {
            logger.info(e.getMessage());
            throw e;
        }
        return dataSource.getConnection();
    }

    public static void closeConnectionPool() {
        dataSource.close();
    }

    public static Integer getActiveConnections() {
        return dataSource.getHikariPoolMXBean().getActiveConnections();
    }

    public static Integer getIdleConnections() {
        return dataSource.getHikariPoolMXBean().getIdleConnections();
    }

    public static Integer getAwaitingConnections() {
        return dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection();
    }
    
}
