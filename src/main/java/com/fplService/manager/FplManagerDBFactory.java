package com.fplService.manager;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseUtils.DatasourcePool;

public class FplManagerDBFactory {

    Connection dbConnection = null;

    public void batchStoreManager(List<FplManager> managers) {

        Logger logger = LoggerFactory.getLogger(FplManagerDBFactory.class);
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();
            
            String insertQuery = "INSERT INTO fpl_managers(manager_id, first_name, second_name, team_name) VALUES (?, ?, ?, ?)";            
            PreparedStatement pStmt = dbConnection.prepareStatement(insertQuery);
            logger.debug("Batch size: " + managers.size());

            for (FplManager manager : managers) {
                buildInsertManagerStatement(manager, pStmt);
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

    public Integer getManagerCount() throws SQLException {

        Integer managerCount = -1;
        PreparedStatement pStmt = null;
        ResultSet managerCountQuery = null;
        
        try {
            dbConnection = DatasourcePool.getDatabaseConnection();    
            
            String selectQuery = "Select count(*) managerCount FROM fpl_managers";
            pStmt = dbConnection.prepareStatement(selectQuery);

            managerCountQuery = executeQueryStatement(pStmt);
            managerCountQuery.next();
            managerCount =  managerCountQuery.getInt("managerCount");
                        
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            managerCountQuery.close();
            pStmt.close();
            dbConnection.close();
        }

        return managerCount;
    }

    public Integer getGameweekCount() throws SQLException {

        Integer gameweekCount = -1;
        PreparedStatement pStmt = null;
        ResultSet gameweekResultSet = null;

        try {
            dbConnection = DatasourcePool.getDatabaseConnection();    
            
            String selectQuery = "Select count(*) gameweekCount FROM fpl_gameweeks";
            pStmt = dbConnection.prepareStatement(selectQuery);

            gameweekResultSet = executeQueryStatement(pStmt);
            gameweekResultSet.next();
            gameweekCount =  gameweekResultSet.getInt("gameweekCount");
                        
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            gameweekResultSet.close();
            pStmt.close();
            dbConnection.close();
        }

        return gameweekCount;
    }

    public void deleteAllManagers() throws SQLException {

        dbConnection = DatasourcePool.getDatabaseConnection();
        String deleteQuery = "DELETE FROM fpl_managers";
        PreparedStatement pStmt = dbConnection.prepareStatement(deleteQuery);
        executeStatement(pStmt);
        dbConnection.close();
         
    }

    public void deleteAllGameweeks() throws SQLException {

        dbConnection = DatasourcePool.getDatabaseConnection();

        String deleteQuery = "DELETE FROM fpl_gameweeks";
        PreparedStatement pStmt = dbConnection.prepareStatement(deleteQuery);
        executeStatement(pStmt);
        dbConnection.close();
         
    }

    private void buildInsertManagerStatement(FplManager manager, PreparedStatement pStmt) throws SQLException {
        
        pStmt.setInt(1, manager.getManagerId());
        pStmt.setString(2, manager.getManagerFirstName());
        pStmt.setString(3, manager.getManagerLastName());
        pStmt.setString(4, manager.getTeamName());

    }

    private void executeStatement(PreparedStatement pStmt) throws SQLException {
        
        try {
            pStmt.executeUpdate();
            pStmt.close();
        } catch (SQLException e) {
            pStmt.close();
            if (e.getErrorCode()== 0) {
                // e.printStackTrace();
            } else { 
                e.printStackTrace();
            } 
        } finally {
            pStmt.close();
        }
    }

    private ResultSet executeQueryStatement(PreparedStatement pStmt) throws SQLException {
        ResultSet results = null;
        try {
            results = pStmt.executeQuery();
            System.out.println(results.isClosed());

            
        } catch (Exception e) {
            pStmt.close();
            e.printStackTrace();
        } finally {
            // stmt.close();
            // pStmt.close();
        }
        
        System.out.println(results.isClosed());
        return results;

    }

}
