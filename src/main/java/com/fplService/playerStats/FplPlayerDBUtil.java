package com.fplService.playerStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseUtils.DatasourcePool;

public class FplPlayerDBUtil {
    
    Logger logger;
    Connection dbConnection = null;

    public void batchStorePlayers(FplPlayerList playerList) {

        logger = LoggerFactory.getLogger(FplPlayerDBUtil.class);
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();
            
            String insertQuery = "INSERT INTO fpl_players(player_id, player_first_name, player_second_name, team_id, "
                + " position_id, selected_by_percent, player_value, player_position) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";            
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
        // now_cost, player_position
        pStmt.setInt(1, player.getId());
        pStmt.setString(2, player.getFirst_name());
        pStmt.setString(3, player.getSecond_name());
        pStmt.setInt(4, player.getTeam());
        pStmt.setInt(5, player.getElement_type());
        pStmt.setFloat(6, player.getSelected_by_percent());
        pStmt.setInt(7, player.getNow_cost());
        pStmt.setString(8, player.position_converter());

    }

    public void deleteAllPlayers() throws SQLException {

        dbConnection = DatasourcePool.getDatabaseConnection();
        String deleteQuery = "DELETE FROM fpl_players";
        dbConnection.createStatement().execute(deleteQuery);
        dbConnection.close();
    }

    public void batchStoreTransferHistories(FplPlayerList playerList) {

        logger = LoggerFactory.getLogger(FplPlayerDBUtil.class);
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();
            
            String insertQuery = "INSERT INTO transfer_histories("
                +"item_id, player_id, "
                +"transfers_in, transfers_in_event, "
                +"transfers_out, transfers_out_event, "
                +"update_time, insert_event_id,"
                +"player_cost, penalties_order,"
                +"selected_by_percent, price_change"
                +") "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";     
                   
            PreparedStatement pStmt = dbConnection.prepareStatement(insertQuery);
            logger.debug("Batch size: " + playerList.elements.length);

            Long insertEventId = System.currentTimeMillis();
            for (FplPlayer player : playerList.elements) {
                buildInsertTransferHistories(player, pStmt, insertEventId);
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

    
    private void buildInsertTransferHistories(FplPlayer player, PreparedStatement pStmt, Long insertEventId) throws SQLException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Long timestampEpoch = System.currentTimeMillis();
        
        pStmt.setString(1, player.getId() + "-" + timestampEpoch);
        pStmt.setInt(2, player.getId());
        pStmt.setInt(3, player.getTransfers_in());
        pStmt.setInt(4, player.getTransfers_in_event());
        pStmt.setInt(5, player.getTransfers_out());
        pStmt.setInt(6, player.getTransfers_out_event());
        pStmt.setTimestamp(7, timestamp);
        pStmt.setLong(8, insertEventId);
        pStmt.setInt(9, player.getNow_cost());
        pStmt.setInt(10, player.getPenalties_order());
        pStmt.setFloat(11, player.getSelected_by_percent());
        pStmt.setInt(12, player.getCost_change_event());

    }
    

}
