package com.fplService.main;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseConnection.DatabaseUtilHelper;
import com.fplService.databaseUtils.DatasourcePool;
import com.fplService.gameweek.FplGameweekFactory;
import com.fplService.gameweek.GameweekConsumer;
import com.fplService.gameweek.GameweekConsumerCloser;
import com.fplService.gameweek.GameweekProducer;
import com.fplService.league.FplLeague;
import com.fplService.league.FplLeagueFactory;
import com.fplService.manager.FplManagerDBFactory;
import com.fplService.manager.FplManagerFactory;
import com.fplService.manager.ManagerConsumer;
import com.fplService.manager.ManagerConsumerCloser;
import com.fplService.manager.ManagerProducer;

public class MainTest {
    
    public static DatabaseUtilHelper databaseHelper;
    static CountDownLatch latch;
    Logger logger;

    
    @Before
    public void initiateDatabaseHelper() throws SQLException {
        logger = LoggerFactory.getLogger(MainTest.class);
        databaseHelper = new DatabaseUtilHelper();
        databaseHelper.startConnections();
        clearDownData();
    }
    
    @Before
    public void setupProducers() {
        new GameweekProducer();
        new ManagerProducer();
    }
    // 
    @Before
    public void setupConsumers() {
        latch = new CountDownLatch(2);

        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        new Thread(managerConsumer).start();
        Runtime.getRuntime().addShutdownHook(new Thread(new ManagerConsumerCloser(managerConsumer)));
        
        GameweekConsumer gameweekConsumer = new GameweekConsumer(latch);
        new Thread(gameweekConsumer).start();
        Runtime.getRuntime().addShutdownHook(new Thread(new GameweekConsumerCloser(gameweekConsumer)));
    }

    @After
    public void tearDown() {
        databaseHelper.closeConnection();
        closeDatabase();
        closeProducers();        

    }

    @Test
    public void testMainFlow() {

        logger.debug("Log message Starting");
        logger.info("Manager Records: " + databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable));

        Integer managerCount = 0;
        Integer gameweekCount=0;
        
        try {
            
            // Integer leagueId = 57365;
            Integer leagueId = 11;
            
            // Get the league details
            FplLeague fplLeague = new FplLeagueFactory().createFplLeage(leagueId);
            List<Integer> managerIds = fplLeague.getManagerIds();
            managerCount = managerIds.size();
            logger.info("Managers: " + managerCount);
        
            // Create all the managers in the league
             for (Integer managerId : managerIds) {
                new FplManagerFactory().createFplManager(managerId);
             }

            // Get the gameweek totals for each manager.
            List<Integer> managersToRemove = new ArrayList<>();
            for (Integer managerId : managerIds) {
                if (!populateGameweekTotals(managerId)) {
                    managersToRemove.add(managerId);
                };
            }
            logger.info("Removing managers " + managersToRemove.size());
            for (Integer managerId : managersToRemove) {
                if (managerIds.remove(managerId)) {
                    logger.debug("Removing manager. New Size " + managerIds.size());
                } 
             } 
             
             gameweekCount = managerIds.size() * 36;
             logger.info("Expected gameweeks: " + gameweekCount);

            // Wait a little bit for the messages to be processed on the separate threads
            checkDatabaseUpdates();
            
            latch.await(5, TimeUnit.SECONDS);
        } catch (Exception e){
            e.printStackTrace();   
        }

        assertEquals(managerCount, databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable));
        assertEquals(gameweekCount, databaseHelper.getRecordCount(DatabaseUtilHelper.fplGameweekTable));

    }

    private void checkDatabaseUpdates() throws InterruptedException {
        Integer databaseCount = 0;
        Boolean isUpdating = true;
        while (isUpdating) {
            Integer newCount = databaseHelper.getRecordCount(DatabaseUtilHelper.fplGameweekTable);
            logger.debug("DatabaseCount: " + databaseCount + ", newCount" + newCount);
            if (databaseCount.equals(newCount)) {
                isUpdating = false;
                logger.debug("Databases A: " + databaseCount);

            } else {
                databaseCount = newCount;
                Thread.sleep(3000);
                logger.debug("Databases B: " + databaseCount);
            };
        }
        logger.debug("Databases C: " + databaseCount);
    }


    private static void closeProducers() {
        GameweekProducer.closeProducer();
        ManagerProducer.closeProducer();
    }

    private static void closeDatabase() {
        DatasourcePool.closeConnectionPool();
    }

    private static boolean populateGameweekTotals(Integer managerId) {
        return new FplGameweekFactory().createManagerGameweek(managerId);
    }

    public void clearDownData() throws SQLException {
        new FplManagerDBFactory().deleteAllManagers();
        new FplManagerDBFactory().deleteAllGameweeks();
        
    }

}
