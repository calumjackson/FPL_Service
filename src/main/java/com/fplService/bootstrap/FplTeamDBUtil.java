package com.fplService.bootstrap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseUtils.DatasourcePool;

public class FplTeamDBUtil {
    
    Logger logger;
    Connection dbConnection = null;

    public void batchStoreTeams(FplTeamList playerList) {

        logger = LoggerFactory.getLogger(FplTeamDBUtil.class);
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();
            
            String insertQuery = "INSERT INTO fpl_teams(team_id, team_name)" 
            + " VALUES (?, ?)";            
            PreparedStatement pStmt = dbConnection.prepareStatement(insertQuery);
            logger.debug("Batch size: " + playerList.teams.length);

            for (FPLTeams player : playerList.teams) {
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

    

    private void buildInsertPlayerStatement(FPLTeams team, PreparedStatement pStmt) throws SQLException {
        
        // player_id, player_first_name, player_second_name, 
        // team_id, position_id, selected_by_percent
        // now_cost
        pStmt.setInt(1, team.getId());
        pStmt.setString(2, team.getName());
        

    }

    public void deleteAllTeams() throws SQLException {

        dbConnection = DatasourcePool.getDatabaseConnection();
        String deleteQuery = "DELETE FROM fpl_teams";
        dbConnection.createStatement().execute(deleteQuery);
        dbConnection.close();
    }
    

}
