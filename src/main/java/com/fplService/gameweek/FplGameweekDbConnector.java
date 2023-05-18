package com.fplService.gameweek;

import java.sql.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseUtils.FplDatabaseConnector;
import com.google.gson.Gson;

public class FplGameweekDbConnector {

    Connection dbConnection;

    public void storeGameweeksJSON(ConsumerRecords<String, String> records) {

        Logger logger = LoggerFactory.getLogger(FplDatabaseConnector.class);

        try {
            dbConnection = FplDatabaseConnector.getFplDbConnection();
       
            for (ConsumerRecord<String, String> record : records) {
                logger.debug("topic = %s, partition = %d, offset = %d, " +
                        "customer = %s, country = %s\n",
                        record.topic(), record.partition(), record.offset(),
                        record.key(), record.value());

                storeGameweekFromJSON(record.value());
        }

        } catch (SQLException e) {
            logger.info(e.getMessage());        
        } 

    }

    public void storeGameweekFromJSON(String gameweekJSON) {
        storeGameweek(new Gson().fromJson((gameweekJSON), FplGameweek.class));
    }

    public void storeGameweek(FplGameweek gameweek)  {
        
        try {
            
            String insertQuery = "INSERT INTO fpl_gameweeks(manager_id, gameweek_id, season_id, week_points, bench_points, transfer_point_deductions) VALUES (?, ?, ?, ?, ?, ?)";
            Statement stmt = dbConnection.createStatement();
            PreparedStatement pStmt = dbConnection.prepareStatement(insertQuery);

            buildInsertGameweekStatement(gameweek, pStmt);
            executeStatement(stmt, pStmt);

        } catch (SQLException e) {
            if (e.getErrorCode()== 0) {
                e.printStackTrace();
            } else { 
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

    private void executeStatement(Statement stmt, PreparedStatement pStmt) throws SQLException {
        try {
            pStmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            stmt.close();
            pStmt.close();
        }
    }

    



    
}
