package com.fplService.gameweek;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseUtils.DatasourcePool;
import com.fplService.databaseUtils.FplDatabaseConnector;

public class FplGameweekDbConnector {

    Connection dbConnection;

    public void batchStoreGameweeks(List<FplGameweek> fplGameweekList) {

        Logger logger = LoggerFactory.getLogger(FplDatabaseConnector.class);
        
        String insertQuery = "INSERT INTO fpl_gameweeks(manager_id, gameweek_id, season_id, week_points, bench_points, transfer_point_deductions) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pStmt = null;
        
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();
            pStmt = dbConnection.prepareStatement(insertQuery);
            
            for (FplGameweek gameweek : fplGameweekList) {
                buildInsertGameweekStatement(gameweek, pStmt);
                pStmt.addBatch();
            }
            int[] updateCounts = pStmt.executeBatch();
            logger.debug(Arrays.toString(updateCounts));


        } catch (SQLException e) {
            
            if (e.getErrorCode()== 0) {
                e.printStackTrace();
            } else { 
                e.printStackTrace();
            } 
        } finally {

            try {
                pStmt.close();
                dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
        
    private void buildInsertGameweekStatement(FplGameweek gameweek, PreparedStatement pStmt) throws SQLException {
        
        pStmt.setInt(1, gameweek.getManagerId());
        pStmt.setInt(2, gameweek.getGameweekId());
        pStmt.setString(3, gameweek.getSeasonId());
        pStmt.setInt(4, gameweek.getGameweekPoints());
        pStmt.setInt(5, gameweek.getGameweekBenchPoints());
        pStmt.setInt(6, gameweek.getTransferPointCosts());

    }

}
