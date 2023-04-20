package com.fplService.gameweek;

import java.sql.*;

import com.fplService.databaseConnection.FplDatabaseConnector;

public class FplGameweekDbConnector {
    
    Connection dbConnection;

    public void storeGameweek(FplGameweek gameweek)  {
        
        FplDatabaseConnector dbConnector = null;
        dbConnector = null;
        
        try {
            
            dbConnector = new FplDatabaseConnector();
            dbConnection = dbConnector.getFplDbConnection();

            String insertQuery = "INSERT INTO fpl_gameweeks(manager_id, gameweek_id, season_id, week_points, bench_points, transfer_point_deductions) VALUES (?, ?, ?, ?, ?, ?)";
            
            Statement stmt = dbConnection.createStatement();
            PreparedStatement pStmt = dbConnection.prepareStatement(insertQuery);

            buildInsertGameweekStatement(gameweek, pStmt);
            executeStatement(stmt, pStmt);

        } catch (SQLException e) {
            if (e.getErrorCode()== 0) {
                // e.printStackTrace();
            } else { 
                e.printStackTrace();
            } 
        } finally {
                
            try {
                dbConnector.closeDatabaseConnection(dbConnection);
            } catch (SQLException e) {
                e.printStackTrace();
            }        }

    }
        
    

    private void buildInsertGameweekStatement(FplGameweek gameweek, PreparedStatement pStmt) throws SQLException {
        
        pStmt.setInt(1, gameweek.getManagerId());
        pStmt.setInt(2, gameweek.getGameweekId());
        pStmt.setString(3, gameweek.getSeasonId());
        pStmt.setInt(4, gameweek.getGameweekPoints());
        pStmt.setInt(5, gameweek.getGameweekBenchPoints());
        pStmt.setInt(6, gameweek.getTransferPointCosts());

    }

    private void executeStatement(Statement stmt, PreparedStatement pStmt) throws SQLException {
        try {
            pStmt.executeUpdate();
            pStmt.close();
        } catch (SQLException e) {
            throw e;
        } finally {
            stmt.close();
            pStmt.close();
        }
    }
}
