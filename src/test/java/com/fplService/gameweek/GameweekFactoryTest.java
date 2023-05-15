package com.fplService.gameweek;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseConnection.DatabaseUtilHelper;

public class GameweekFactoryTest {
    
    private DatabaseUtilHelper databaseHelper;
    private Logger logger;

    @Before
    public void setupTestConfigs() {
        logger = LoggerFactory.getLogger(GameweekFactoryTest.class);
        databaseHelper = new DatabaseUtilHelper();
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
    }

    static String testManagerId = "638102";
    static String testGameweekJSON = "{\"managerId\": \""+testManagerId+"\", " + 
    "\"gameweekId\": \"1\", " +
    "\"seasonId\": \"2022-23\", " + 
    "\"gameweekPoints\": \"75\", " +
    "\"gameweekBenchPoints\": \"0\", " +
    "\"transferPointCosts\": \"6\"}";


    public void publishMessage(String testMessage) {

        String boostrapServers = "localhost:9092"; 
    
            Properties producerProps = new Properties();
            producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
            producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    
            KafkaProducer<String, String> testProducer = new KafkaProducer<>(producerProps);
            ProducerRecord<String, String> testManagerRecord = new ProducerRecord<String, String>(GameweekProducer.GAMEWEEK_TOPIC, testMessage);

            testProducer.send(testManagerRecord);
            testProducer.flush();
            testProducer.close();
    }

    @Test
    public void testReceiveMessage() {

        FplGameweekDbConnector fplGameweekDBFactory = mock(FplGameweekDbConnector.class);
        doNothing().when(fplGameweekDBFactory).storeGameweekFromJSON(anyString());

        assertFalse(databaseHelper.doesGameweekRecordExist(testManagerId));

        publishMessage(testGameweekJSON);
        CountDownLatch latch = new CountDownLatch(1);
        GameweekConsumer gameWeekConsumer = new GameweekConsumer(latch);
        
        gameWeekConsumer.pollGameweekConsumer(Duration.ofMillis(15000));

        gameWeekConsumer.closeConsumer();

        assertTrue(databaseHelper.doesGameweekRecordExist(testManagerId));
    
    }
    


}
