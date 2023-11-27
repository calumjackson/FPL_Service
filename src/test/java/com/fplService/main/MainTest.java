package com.fplService.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.bootstrap.FPLGameweekNumberDecoder;
import com.fplService.databaseConnection.DatabaseUtilHelper;
import com.fplService.databaseUtils.DatasourcePool;
import com.fplService.gameweek.GameweekConsumer;
import com.fplService.gameweek.GameweekConsumerCloser;
import com.fplService.gameweek.GameweekCounter;
import com.fplService.gameweek.GameweekCounterCloser;
import com.fplService.gameweek.GameweekProducer;
import com.fplService.league.FplLeague;
import com.fplService.league.FplLeagueFactory;
import com.fplService.manager.FplManagerDBUtil;
import com.fplService.manager.FplManagerFactory;
import com.fplService.manager.ManagerConsumer;
import com.fplService.manager.ManagerConsumerCloser;
import com.fplService.manager.ManagerGameweekGenerator;
import com.fplService.manager.ManagerGameweekGeneratorCloser;
import com.fplService.manager.ManagerProducer;

public class MainTest {
    
    public static DatabaseUtilHelper databaseHelper;
    static CountDownLatch latch;
    static Logger logger;
    Integer numberOfGameweeks = new FPLGameweekNumberDecoder().getCurrentGameweekId();
    Integer maxLeagueSize = 500;

    @BeforeClass
    public static void initiateDatabaseHelper() throws SQLException {
        logger = LoggerFactory.getLogger(MainTest.class);
        databaseHelper = new DatabaseUtilHelper();
        databaseHelper.startConnections();
    }

    @Before
    public void clearDB() throws SQLException {
        logger = LoggerFactory.getLogger(MainTest.class);
        clearDownData();
    }    
    
    @BeforeClass
    public static void setupProducers() {
        new GameweekProducer();
        new ManagerProducer();
    }
    
    @BeforeClass
    public static void setupConsumers() {
        latch = new CountDownLatch(3);

        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        new Thread(managerConsumer).start();
        Runtime.getRuntime().addShutdownHook(new Thread(new ManagerConsumerCloser(managerConsumer)));

        ManagerGameweekGenerator managerGameweekGeneratorConsumer = new ManagerGameweekGenerator(latch);
        new Thread(managerGameweekGeneratorConsumer).start();
        Runtime.getRuntime().addShutdownHook(new Thread(new ManagerGameweekGeneratorCloser(managerGameweekGeneratorConsumer)));
        
        GameweekConsumer gameweekConsumer = new GameweekConsumer(latch);
        new Thread(gameweekConsumer).start();
        Runtime.getRuntime().addShutdownHook(new Thread(new GameweekConsumerCloser(gameweekConsumer)));

        GameweekCounter gameweekCounter = new GameweekCounter(latch);
        new Thread(gameweekCounter).start();
        Runtime.getRuntime().addShutdownHook(new Thread(new GameweekCounterCloser(gameweekCounter)));
    }

    @After
    public void tearDownCounter() {
        GameweekCounter.resetGameweekCounter();  
    }

    @AfterClass
    public static void tearDown() {
        databaseHelper.closeConnection();
        closeDatabase();
        closeProducers();      
        GameweekCounter.resetGameweekCounter();
    }

    @Test
    public void testFlowWithLadsLeague() {
        Integer leagueId = 117288;
        testMainFlow(leagueId, maxLeagueSize);
    }

    @Test
    public void testFlowWithtwoPageLeague() {
        Integer leagueId = 57380;
        // 1860 
        testMainFlow(leagueId, maxLeagueSize);
    }

    @Test
    public void testFlowWithLargerLeague() {
        Integer leagueId = 11;
        testMainFlow(leagueId, maxLeagueSize);
    }

    @Test
    public void testFlowWithInvalidTeamName() {
        Integer leagueId = 1150143;
        testMainFlow(leagueId, maxLeagueSize);
    }



    @Test
    public void testFlowWithLargestLeague() {
        Integer maxLeagueSize = 5000;
        Integer leagueId = 11;
        testMainFlow(leagueId, maxLeagueSize);
    }

    public void testMainFlow(Integer leagueId, Integer maxLeagueSize) {

        logger.debug("Log message Starting");
        logger.info("Manager Records: " + databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable));

        Integer managerCount = 0;
        Integer gameweekCount = 0;
        
        try {           
            // Get the league details
            FplLeague fplLeague = new FplLeagueFactory().requestFplLeagueDetails(leagueId, maxLeagueSize);
            List<Integer> managerIds = fplLeague.getManagerIds();
            managerCount = managerIds.size();
            logger.info("Managers: " + managerCount);
        
            List<Integer> failedManagerIds = new ArrayList<>();

            // Create all the managers in the league
            for (Integer managerId : managerIds) {
                try {
                    new FplManagerFactory().createFplManager(managerId);
                } catch (Exception e) {
                    logger.info(e.getMessage());
                    failedManagerIds.add(managerId);
                    Thread.sleep(100);
                }
            }

            for (Integer managerId : failedManagerIds) {
                try {
                    new FplManagerFactory().createFplManager(managerId);
                } catch (Exception e) {
                    e.printStackTrace();                
                }
            }


            
            // Wait a little bit for the messages to be processed on the separate threads
            checkDatabaseUpdates(DatabaseUtilHelper.fplManagersTable);
            checkDatabaseUpdates(DatabaseUtilHelper.fplGameweekTable);
            
            logger.info("Expected gameweeks: " + gameweekCount + " compared to " + GameweekCounter.getGameweekCounter());
        } catch (Exception e){
            e.printStackTrace();   
        }

        logger.info("Gameweeks to expect: " + GameweekCounter.getGameweekCounter());
        Integer zeroval=0;
        assertNotEquals(zeroval, databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable));
        assertEquals(managerCount, databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable));
        assertEquals(GameweekCounter.getGameweekCounter(), databaseHelper.getRecordCount(DatabaseUtilHelper.fplGameweekTable));

    }

    private void checkDatabaseUpdates(String tableName) throws InterruptedException {
        Integer sleepTimer = 1500;
        Integer databaseCount = 0;
        Boolean isUpdating = true;
        Integer attemptcount = 0;

        while (isUpdating) {
            Integer newCount = databaseHelper.getRecordCount(tableName);
            logger.debug("DatabaseCount: " + databaseCount + ", newCount" + newCount);
            if(attemptcount == 0) {
                databaseCount = newCount;
                attemptcount++;
            } else if (databaseCount.equals(newCount)) {
                logger.info("Databases A: " + databaseCount);
                Thread.sleep(sleepTimer);
                if (attemptcount > 2) {
                    isUpdating=false;
                }
                attemptcount++;

            } else  {
                databaseCount = newCount;
                Thread.sleep(sleepTimer);
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
    public void testDatabaseCheck5k() throws InterruptedException {
        Integer gameweeksToGenerate = 5000;
        generateGameweeks(gameweeksToGenerate);
        Thread.sleep(1000);
        try {
            checkDatabaseUpdates(DatabaseUtilHelper.fplGameweekTable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            logger.debug(testMessage);
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

    public void clearDownData() throws SQLException {
        new FplManagerDBUtil().deleteAllManagers();
        new FplManagerDBUtil().deleteAllGameweeks();
    }

}
