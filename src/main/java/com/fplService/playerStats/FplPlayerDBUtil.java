package com.fplService.playerStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseUtils.DatasourcePool;

public class FplPlayerDBUtil {
    
    Logger logger;
    Connection dbConnection = null;

    public void batchStoreManager(FplPlayerList playerList) {

        logger = LoggerFactory.getLogger(FplPlayerDBUtil.class);
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();
            
            String insertQuery = "INSERT INTO fpl_players(player_id, player_first_name, player_second_name, team_id, position_id, selected_by_percent, player_value) VALUES (?, ?, ?, ?, ?, ?, ?)";            
            PreparedStatement pStmt = dbConnection.prepareStatement(insertQuery);
            logger.debug("Batch size: " + playerList.elements.length);

            for (FplPlayer player : playerList.elements) {
                buildInsertPlayerStatement(player, pStmt);
                pStmt.addBatch();
            }
            int[] updateCounts = pStmt.executeBatch();
            logger.debug(Arrays.toString(updateCounts));
            
        } catch (SQLException e) {
            logger.info(e.getMessage());
        } finally {
            try {
                dbConnection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void buildInsertPlayerStatement(FplPlayer player, PreparedStatement pStmt) throws SQLException {
        
        // player_id, player_first_name, player_second_name, 
        // team_id, position_id, selected_by_percent
        // now_cost
        pStmt.setInt(1, player.getId());
        pStmt.setString(2, player.getFirst_name());
        pStmt.setString(3, player.getSecond_name());
        pStmt.setInt(4, player.getTeam());
        pStmt.setInt(5, player.getElement_type());
        pStmt.setFloat(6, player.getSelected_by_percent());
        pStmt.setInt(7, player.getNow_cost());

    }

    public void deleteAllPlayers() throws SQLException {

        dbConnection = DatasourcePool.getDatabaseConnection();
        String deleteQuery = "DELETE FROM fpl_players";
        PreparedStatement pStmt = dbConnection.prepareStatement(deleteQuery);
        pStmt.executeQuery();
        pStmt.close();
        dbConnection.close();
    }
    

}
