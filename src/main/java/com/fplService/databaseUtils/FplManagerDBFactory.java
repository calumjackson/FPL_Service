package com.fplService.databaseUtils;

import java.sql.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.manager.FplManager;
import com.google.gson.Gson;

public class FplManagerDBFactory {

    Connection dbConnection;
    
    public void storeManagersJSON(ConsumerRecords<String, String> records) {

        Logger logger = LoggerFactory.getLogger(FplManagerDBFactory.class);

        try {
            dbConnection = FplDatabaseConnector.getFplDbConnection();
        

            for (ConsumerRecord<String, String> record : records) {
                logger.debug("topic = %s, partition = %d, offset = %d, " +
                        "customer = %s, country = %s\n",
                        record.topic(), record.partition(), record.offset(),
                        record.key(), record.value());

                storeManagerFromJSON(record.value());
            }

        } catch (SQLException e) {
            logger.info(e.getMessage());
        } 

    }


    public void storeManager(FplManager manager) {
        try {
            
            String insertQuery = "INSERT INTO fpl_managers(manager_id, first_name, second_name, team_name) VALUES (?, ?, ?, ?)";            
            Statement stmt = dbConnection.createStatement();
            PreparedStatement pStmt = dbConnection.prepareStatement(insertQuery);
            
            buildInsertManagerStatement(manager, pStmt);
            executeStatement(stmt, pStmt);
            
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }

    public void storeManagerFromJSON(String managerJSON) {
        storeManager(new Gson().fromJson((managerJSON), FplManager.class));
    }

    public Integer getManagerCount() throws SQLException {

        Integer managerCount = -1;
        Statement stmt = null;
        PreparedStatement pStmt = null;
        ResultSet managerCountQuery = null;

        try {
            dbConnection = FplDatabaseConnector.getFplDbConnection();    
            
            String selectQuery = "Select count(*) managerCount FROM fpl_managers";
            stmt = dbConnection.createStatement();
            pStmt = dbConnection.prepareStatement(selectQuery);

            managerCountQuery = executeQueryStatement(stmt, pStmt);
            managerCountQuery.next();
            managerCount =  managerCountQuery.getInt("managerCount");
                        
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            managerCountQuery.close();
            dbConnection.close();
            closeStatements(stmt, pStmt);
        }

        return managerCount;
    }

    public Integer getGameweekCount() throws SQLException {

        Integer gameweekCount = -1;
        Statement stmt = null;
        PreparedStatement pStmt = null;
        ResultSet gameweekResultSet = null;

        try {
            dbConnection = FplDatabaseConnector.getFplDbConnection();    
            
            String selectQuery = "Select count(*) gameweekCount FROM fpl_gameweeks";
            stmt = dbConnection.createStatement();
            pStmt = dbConnection.prepareStatement(selectQuery);

            gameweekResultSet = executeQueryStatement(stmt, pStmt);
            gameweekResultSet.next();
            gameweekCount =  gameweekResultSet.getInt("gameweekCount");
                        
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            gameweekResultSet.close();
            closeStatements(stmt, pStmt);
        }

        return gameweekCount;
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
        } catch (SQLException e) {
            stmt.close();
            pStmt.close();
            if (e.getErrorCode()== 0) {
                // e.printStackTrace();
            } else { 
                e.printStackTrace();
            } 
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