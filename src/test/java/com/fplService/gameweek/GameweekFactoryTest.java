package com.fplService.gameweek;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseConnection.DatabaseUtilHelper;
import com.fplService.databaseUtils.DatasourcePool;
import com.fplService.databaseUtils.FplDatabaseConnector;

public class GameweekFactoryTest {
    
    private DatabaseUtilHelper databaseHelper;
    private Logger logger;

    @Before
    public void setupTestConfigs() throws SQLException {
        DatasourcePool.initiateDatabasePool();
        FplDatabaseConnector.getFplDbConnection();
        logger = LoggerFactory.getLogger(GameweekFactoryTest.class);
        databaseHelper = new DatabaseUtilHelper();
        GameweekProducer.createGameweekProducer();
        try {
            databaseHelper.deleteAllRecordsFromTable(DatabaseUtilHelper.fplGameweekTable);
        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
    }

    @After
    public void closeDatabase() {
        try {
            databaseHelper.deleteAllRecordsFromTable(DatabaseUtilHelper.fplGameweekTable);
        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
        databaseHelper.closeConnection();
        GameweekProducer.closeProducer();
    }

    static String testManagerId = "638102";
    static String testGameweekJSON = "{\"managerId\": \""+testManagerId+"\", " + 
    "\"gameweekId\": \"1\", " +
    "\"seasonId\": \"2022-23\", " + 
    "\"gameweekPoints\": \"75\", " +
    "\"gameweekBenchPoints\": \"0\", " +
    "\"transferPointCosts\": \"6\"}";


    public void publishMessage(String testMessage) {

            ProducerRecord<String, String> testManagerRecord = new ProducerRecord<String, String>(GameweekProducer.GAMEWEEK_TOPIC, testMessage);

            GameweekProducer.sendMessage(testManagerRecord);
    }

    @Test
    public void testReceiveMessage() throws SQLException {
        
        assertFalse(databaseHelper.doesGameweekRecordExist(testManagerId));
        assertNotNull(DatasourcePool.getDatabaseConnection());
        
        publishMessage(testGameweekJSON);
        GameweekProducer.closeProducer();
        GameweekConsumer gameWeekConsumer = new GameweekConsumer(new CountDownLatch(1));
        
        gameWeekConsumer.pollGameweekConsumer(Duration.ofMillis(15000));

        gameWeekConsumer.closeConsumer();

        assertTrue(databaseHelper.doesGameweekRecordExist(testManagerId));

    }

    @Test
    public void testReceiveScaleMessage() {

        Integer scale = 1000;
        
        assertFalse(databaseHelper.doesGameweekRecordExist(testManagerId));
        
        GameweekConsumer gameWeekConsumer = new GameweekConsumer(new CountDownLatch(1));
        new Thread(gameWeekConsumer).start();
        
        for (int x =1; x <= scale; x++) { 
            publishMessage(getGameweekTestString(x));
        }
        GameweekProducer.closeProducer();
        
        Runtime.getRuntime().addShutdownHook(new Thread(new GameweekConsumerCloser(gameWeekConsumer)));


        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        assertEquals(scale, databaseHelper.getRecordCount(DatabaseUtilHelper.fplGameweekTable));
    }

    private String getGameweekTestString(Integer gameweekNumber) {

        String gameweekJSON = "{\"managerId\": \""+testManagerId+"\", " + 
                "\"gameweekId\": \""+gameweekNumber+"\", " +
                "\"seasonId\": \"2022-23\", " + 
                "\"gameweekPoints\": \"75\", " +
                "\"gameweekBenchPoints\": \"0\", " +
                "\"transferPointCosts\": \"6\"}";

        return gameweekJSON;

    }

}