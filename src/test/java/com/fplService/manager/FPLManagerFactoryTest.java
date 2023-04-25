package com.fplService.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

public class FPLManagerFactoryTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

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



        assertTrue(outContent.toString().contains(testManagerJson));
        
        
        

    }
    
}
