package com.fplService.main;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
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
    Integer numberOfGameweeks = 37;

    
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
    public void testFlowWithLadsLeague() {
        Integer leagueId = 57365;
        testMainFlow(leagueId);
    }

    @Test
    public void testFlowWithtwoPageLeague() {
        Integer leagueId = 57380;
        testMainFlow(leagueId);
    }

    @Test
    public void testFlowWithLargerLeague() {
        Integer leagueId = 11;
        testMainFlow(leagueId);
    }

    public void testMainFlow(Integer leagueId) {

        logger.debug("Log message Starting");
        logger.info("Manager Records: " + databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable));

        Integer managerCount = 0;
        Integer gameweekCount = 0;
        
        try {           
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
            for (Integer managerId : managerIds) {
                gameweekCount += populateGameweekTotals(managerId);
            }
             
            logger.info("Expected gameweeks: " + gameweekCount);

            // Wait a little bit for the messages to be processed on the separate threads
            checkDatabaseUpdates(DatabaseUtilHelper.fplManagersTable);
            checkDatabaseUpdates(DatabaseUtilHelper.fplGameweekTable);

            
            latch.await(5, TimeUnit.SECONDS);
        } catch (Exception e){
            e.printStackTrace();   
        }

        assertEquals(managerCount, databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable));
        assertEquals(gameweekCount, databaseHelper.getRecordCount(DatabaseUtilHelper.fplGameweekTable));

    }

    private void checkDatabaseUpdates(String tableName) throws InterruptedException {
        Integer databaseCount = 0;
        Boolean isUpdating = true;
        Integer attemptcount = 0;
        while (isUpdating) {
            Integer newCount = databaseHelper.getRecordCount(tableName);
            logger.debug("DatabaseCount: " + databaseCount + ", newCount" + newCount);
            if (databaseCount.equals(newCount)) {
                logger.info("Databases A: " + databaseCount);
                Thread.sleep(3000);
                if (attemptcount > 3) {
                    isUpdating=false;
                }
                attemptcount++;

            } else {
                databaseCount = newCount;
                Thread.sleep(3000);
                logger.info("Databases B: " + databaseCount);
                attemptcount = 1;
            };
        }
        logger.info("Databases C: " + databaseCount);
    }

    @Test 
    public void testDatabaseCheck() {
        Integer gameweeksToGenerate = 1000;
        generateGameweeks(gameweeksToGenerate);
        try {
            checkDatabaseUpdates(DatabaseUtilHelper.fplGameweekTable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(gameweeksToGenerate, databaseHelper.getRecordCount(DatabaseUtilHelper.fplGameweekTable));
    }
    
    @Test 
    public void testDatabaseCheck5k() {
        Integer gameweeksToGenerate = 5000;
        generateGameweeks(gameweeksToGenerate);
        try {
            checkDatabaseUpdates(DatabaseUtilHelper.fplGameweekTable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(gameweeksToGenerate, databaseHelper.getRecordCount(DatabaseUtilHelper.fplGameweekTable));
    }

    private void generateGameweeks(Integer gameweeksToGenerate) {

        String testGameweekJSONStart = "{\"managerId\": \"";
        String testManagerEnd =  "\", \"gameweekId\": \"1\", " +
            "\"seasonId\": \"2022-23\", " + 
            "\"gameweekPoints\": \"75\", " +
            "\"gameweekBenchPoints\": \"0\", " +
            "\"transferPointCosts\": \"6\"}";
        for (int x = 1; x <= gameweeksToGenerate; x++) {
            String testMessage = testGameweekJSONStart + x + testManagerEnd;
            logger.info(testMessage);
            ProducerRecord<String, String> testManagerRecord = new ProducerRecord<String, String>(DatabaseUtilHelper.fplGameweekTable, testMessage);
            GameweekProducer.sendMessage(testManagerRecord);
        }

}

    private static void closeProducers() {
        GameweekProducer.closeProducer();
        ManagerProducer.closeProducer();
    }

    private static void closeDatabase() {
        DatasourcePool.closeConnectionPool();
    }

    private static Integer populateGameweekTotals(Integer managerId) {
        return new FplGameweekFactory().createManagerGameweek(managerId);
    }

    public void clearDownData() throws SQLException {
        new FplManagerDBFactory().deleteAllManagers();
        new FplManagerDBFactory().deleteAllGameweeks();
        
    }

}
