package com.fplService.managerDatabase;

import java.sql.*;

import com.fplService.databaseConnection.FplDatabaseConnector;
import com.fplService.manager.FplManager;

public class FplManagerDBFactory {

    Connection dbConnection;
    

    public void storeManager(FplManager manager) {
        try {

            dbConnection = FplDatabaseConnector.getFplDbConnection();
            
            String insertQuery = "INSERT INTO fpl_managers(manager_id, first_name, second_name, team_name) VALUES (?, ?, ?, ?)";
            
            Statement stmt = dbConnection.createStatement();
            PreparedStatement pStmt = dbConnection.prepareStatement(insertQuery);
            
            buildInsertManagerStatement(manager, pStmt);
            executeStatement(stmt, pStmt);
            
            FplDatabaseConnector.closeDatabaseConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally { 
            try {
                dbConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        
    }

    public Integer getManagerCount() throws SQLException {

        Integer managerCount = -1;
        Statement stmt = null;
        PreparedStatement pStmt = null;
        try {
            dbConnection = FplDatabaseConnector.getFplDbConnection();
            
            
            String selectQuery = "Select count(*) managerCount FROM fpl_managers";
            stmt = dbConnection.createStatement();
            pStmt = dbConnection.prepareStatement(selectQuery);

            ResultSet managerCountQuery = executeQueryStatement(stmt, pStmt);
            managerCountQuery.next();
            managerCount =  managerCountQuery.getInt("managerCount");


            managerCountQuery.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.close();
            closeStatements(stmt, pStmt);
        }

        return managerCount;
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

    private void buildInsertManagerStatement(FplManager manager, PreparedStatement pStmt) throws SQLException {
        
        pStmt.setInt(1, manager.getManagerId());
        pStmt.setString(2, manager.getManagerFirstName());
        pStmt.setString(3, manager.getManagerLastName());
        pStmt.setString(4, manager.getTeamName());

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

    private ResultSet executeQueryStatement(Statement stmt, PreparedStatement pStmt) throws SQLException {
        ResultSet results = null;
        try {
            results = pStmt.executeQuery();
            System.out.println(results.isClosed());

            
        } catch (Exception e) {
            stmt.close();
            pStmt.close();
            e.printStackTrace();
        } finally {
            // stmt.close();
            // pStmt.close();
        }
        
        System.out.println(results.isClosed());
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

}
