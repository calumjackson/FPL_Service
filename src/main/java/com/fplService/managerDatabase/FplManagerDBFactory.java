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

}
