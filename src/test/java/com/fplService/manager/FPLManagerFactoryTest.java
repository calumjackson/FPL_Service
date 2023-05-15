package com.fplService.manager;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.databaseConnection.DatabaseUtilHelper;

public class FPLManagerFactoryTest {

    private DatabaseUtilHelper databaseHelper = null;
    Logger logger;

    @Before
    public void setUpTestManager() {

        logger = LoggerFactory.getLogger(FPLManagerFactoryTest.class);
        this.databaseHelper = new DatabaseUtilHelper();
        try {
            databaseHelper.deleteAllRecordsFromTable(DatabaseUtilHelper.fplManagersTable);
        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
    }

    @After
    public void closeDatabase() throws SQLException {
        databaseHelper.closeConnection();
        databaseHelper.deleteAllRecordsFromTable(DatabaseUtilHelper.fplManagersTable);
    }

    static String testManagerId = "2046938";
    static String testManagerJson = "{ \"managerFirstName\": \"Tom\", " + 
    "\"managerLastName\": \"Litherland\", \"managerId\": \""+testManagerId+"\"," + 
    " \"teamName\": \"Klopps n Robbers\" }";

    static

    public void publishMessage(String testMessage) {

        String boostrapServers = "localhost:9092"; 
    
            Properties producerProps = new Properties();
            producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
            producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    
            KafkaProducer<String, String> testProducer = new KafkaProducer<>(producerProps);
            ProducerRecord<String, String> testManagerRecord = new ProducerRecord<String, String>(ManagerProducer.MANAGER_TOPIC, testMessage);

            testProducer.send(testManagerRecord);
            testProducer.close();
    }

    @Test
    public void testReceiveMessage() {

        assertFalse(databaseHelper.doesManagerRecordExist(testManagerId));

        publishMessage(testManagerJson);
        CountDownLatch latch = new CountDownLatch(1);
        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        
        managerConsumer.pollManagerConsumer(Duration.ofMillis(10000));

        managerConsumer.closeProducer();

        assertTrue(databaseHelper.doesManagerRecordExist(testManagerId));
  
    }
    
}
