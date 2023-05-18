package com.fplService.databaseConnection;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseUtils.DatasourcePool;

public class DatabaseUtilHelper {
    
    Connection dbConnection;
    Logger logger;

    public static String fplManagersTable = "fpl_managers";
    public static String fplGameweekTable = "fpl_gameweeks";

    public DatabaseUtilHelper() {

        logger = LoggerFactory.getLogger(DatabaseUtilHelper.class);
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();
            } catch (Exception e) {
                logger.info(e.getStackTrace().toString());
        }
    }

    public void closeConnection() {
        DatasourcePool.closeConnectionPool();
    }   

    public void deleteAllRecordsFromTable(String tableName) throws SQLException {

        dbConnection = DatasourcePool.getDatabaseConnection();

        String deleteQuery = "DELETE FROM " + tableName;
        Statement stmt = dbConnection.createStatement();
        PreparedStatement pStmt = dbConnection.prepareStatement(deleteQuery);
        executeStatement(stmt, pStmt);
         
        dbConnection.close();
    }


    public boolean doesManagerRecordExist(String managerId) {

        Boolean managerExists = false;
        String tableName = "fpl_managers";

        try {
            dbConnection = DatasourcePool.getDatabaseConnection();    
            
            Integer managerCount = getRecordCount(tableName, managerId);
            logger.info("Test Managers Found: " + managerCount);
            if (managerCount==1) {
                managerExists = true;
            }
                        
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                logger.info(e.getMessage());
            }
        }

        return managerExists;

    }

    public boolean doesGameweekRecordExist(String managerId) {

        Boolean gameweekExists = false;

        try {
            dbConnection = DatasourcePool.getDatabaseConnection(); 
            
            String tableName = "fpl_gameweeks";
            Integer gameweekCount = getRecordCount(tableName, managerId);

            logger.info("Test Gameweeks Found: " + gameweekCount);
            if (gameweekCount==1) {
                gameweekExists = true;
            }
                        
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                logger.info(e.getMessage());
            }
        }

        return gameweekExists;

    }

    public Integer getRecordCount(String tableName, String managerId) { 
        Statement stmt = null;
        PreparedStatement pStmt = null;
        
        
        
        String query = "select count(*) recordCount from " + tableName + " where manager_id = ?";
        Integer recordCount = -1;
        ResultSet gameweekCountQuery = null;
        
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();     
            stmt = dbConnection.createStatement();
            pStmt = dbConnection.prepareStatement(query);
            pStmt.setInt(1, Integer.parseInt(managerId));
            gameweekCountQuery = executeQueryStatement(stmt, pStmt);
            gameweekCountQuery.next();
            recordCount = gameweekCountQuery.getInt("recordCount");
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
            closeStatements(stmt, pStmt);
            closeConnection();
        }
        return recordCount;
    }

    public Integer getRecordCount(String tableName) {
        Integer recordCount = -1;
        ResultSet gameweekCountQuery = null;
        Statement stmt = null;
        PreparedStatement pStmt = null;
        String query = "select count(*) recordCount from " + tableName;

        
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();    
            stmt = dbConnection.createStatement();
             pStmt = dbConnection.prepareStatement(query);
            gameweekCountQuery = executeQueryStatement(stmt, pStmt);
            gameweekCountQuery.next();
            recordCount = gameweekCountQuery.getInt("recordCount");

            logger.info("Record Count: " + recordCount); 
        } catch (Exception e) {
                e.printStackTrace();
                
        } finally {
            closeStatements(stmt, pStmt);
        }
        return recordCount;
    }

    
    private ResultSet executeQueryStatement(Statement stmt, PreparedStatement pStmt) throws SQLException {
        ResultSet results = null;
        try {
            results = pStmt.executeQuery();

            
        } catch (Exception e) {
            stmt.close();
            pStmt.close();
            e.printStackTrace();
        } 
        
        return results;

    }

    private void closeStatements(Statement stmt, PreparedStatement pStmt) {
        try {
            stmt.close();
            pStmt.close();
        } catch (SQLException e) { 
            e.printStackTrace();
        }
    }

    private void executeStatement(Statement stmt, PreparedStatement pStmt) throws SQLException {
        try {
            pStmt.executeUpdate();
            pStmt.close();
        } catch (Exception e) {
            stmt.close();
            pStmt.close();
            e.printStackTrace();
        } finally {
            stmt.close();
            pStmt.close();
        }
    }

    
}
