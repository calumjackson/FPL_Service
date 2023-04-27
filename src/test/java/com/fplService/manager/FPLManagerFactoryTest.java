package com.fplService.manager;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

import com.fplService.managerDatabase.FplManagerDBFactory;

public class FPLManagerFactoryTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private FplManager testFplManager = null;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Before
    public void clearDB() {
        try {
            new FplManagerDBFactory().deleteAllManagers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUpTestManager() {
        this.testFplManager = new FplManager(2046938, "Tom", "Litherland", "Test Team Name");
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    // @After
    // public void burnDownDB() {
    //     try {
    //         new FplManagerDBFactory().deleteAllManagers();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }
    

    static String testManagerJson = "{ \"managerFirstName\": \"Tom\", " + 
    "\"managerLastName\": \"Litherland\", \"managerId\": \"2046938\"," + 
    " \"teamName\": \"Klopps n Robbers\" }";

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

        FplManagerDBFactory fplManagerDBFactory = mock(FplManagerDBFactory.class);
        doNothing().when(fplManagerDBFactory).storeManagerFromJSON(anyString());

        publishMessage(testManagerJson);
        CountDownLatch latch = new CountDownLatch(1);
        ManagerConsumer managerConsumer = new ManagerConsumer(latch);

        managerConsumer.createTeamConsumer();
        
        managerConsumer.pollManagerConsumer(Duration.ofMillis(1000));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        managerConsumer.closeProducer();

        // Check that the system message contains the JSON. Cannot exact search due to offsets changing.
        assertTrue(outContent.toString().contains(testManagerJson));
        
        
        

    }
    
}
