package com.fplService.databaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUtilHelper {
    
    Connection dbConnection;
    Logger logger;

    public DatabaseUtilHelper() {

        logger = LoggerFactory.getLogger(DatabaseUtilHelper.class);
        try {
            dbConnection = FplDatabaseConnector.getFplDbConnection();
            } catch (Exception e) {
                logger.info(e.getStackTrace().toString());
        }
    }

    public void closeConnection() {
        try {
            FplDatabaseConnector.closeDatabaseConnection();
        } catch (SQLException e) {
            logger.info(e.getStackTrace().toString());
        }
    }


    

    public void deleteAllManagers() throws SQLException {

        dbConnection = FplDatabaseConnector.getFplDbConnection();

        String deleteQuery = "DELETE FROM fpl_managers";
        Statement stmt = dbConnection.createStatement();
        PreparedStatement pStmt = dbConnection.prepareStatement(deleteQuery);
        executeStatement(stmt, pStmt);
         
    }

    public void deleteAllGameweeks() throws SQLException {

        dbConnection = FplDatabaseConnector.getFplDbConnection();

        String deleteQuery = "DELETE FROM fpl_gameweeks";
        Statement stmt = dbConnection.createStatement();
        PreparedStatement pStmt = dbConnection.prepareStatement(deleteQuery);
        executeStatement(stmt, pStmt);
         
    }

    public boolean doesManagerRecordExist(String managerId) {

        Statement stmt = null;
        PreparedStatement pStmt = null;
        ResultSet managerCountQuery = null;
        Boolean managerExists = false;

        try {
            dbConnection = FplDatabaseConnector.getFplDbConnection();    
            
            String selectQuery = "Select count(*) managerCount FROM fpl_managers where manager_id = ?";
            stmt = dbConnection.createStatement();
            pStmt = dbConnection.prepareStatement(selectQuery);
            pStmt.setInt(1, Integer.parseInt(managerId));

            
            managerCountQuery = executeQueryStatement(stmt, pStmt);
            managerCountQuery.next();
            Integer managerCount = managerCountQuery.getInt("managerCount");
            logger.info("Test Managers Found: " + managerCount);
            if (managerCount==1) {
                managerExists = true;
            }
                        
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {

                managerCountQuery.close();
                dbConnection.close();
                closeStatements(stmt, pStmt);
            } catch (SQLException e) {
                logger.info(e.getMessage());
            }
        }

        return managerExists;

    }

    public boolean doesGameweekRecordExist(String managerId) {

        Statement stmt = null;
        PreparedStatement pStmt = null;
        ResultSet gameweekCountQuery = null;
        Boolean gameweekExists = false;

        try {
            dbConnection = FplDatabaseConnector.getFplDbConnection();    
            
            String selectQuery = "Select count(*) gameweekCount FROM fpl_gameweeks where manager_id = ?";
            stmt = dbConnection.createStatement();
            pStmt = dbConnection.prepareStatement(selectQuery);
            pStmt.setInt(1, Integer.parseInt(managerId));

            
            gameweekCountQuery = executeQueryStatement(stmt, pStmt);
            gameweekCountQuery.next();
            Integer gameweekCount = gameweekCountQuery.getInt("gameweekCount");
            logger.info("Test Gameweeks Found: " + gameweekCount);
            if (gameweekCount==1) {
                gameweekExists = true;
            }
                        
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {

                gameweekCountQuery.close();
                dbConnection.close();
                closeStatements(stmt, pStmt);
            } catch (SQLException e) {
                logger.info(e.getMessage());
            }
        }

        return gameweekExists;

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
