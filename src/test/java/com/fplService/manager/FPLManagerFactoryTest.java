package com.fplService.manager;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.ProducerRecord;
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
        databaseHelper.startConnections();
        new ManagerProducer();

        try {
            databaseHelper.deleteAllRecordsFromTable(DatabaseUtilHelper.fplManagersTable);
        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
    }

    @After
    public void closeDatabase() throws SQLException {
        databaseHelper.closeConnection();
        // databaseHelper.deleteAllRecordsFromTable(DatabaseUtilHelper.fplManagersTable);

        ManagerProducer.closeProducer();
    }

    static String testManagerId = "2046938";
    static String testManagerJson = "{ \"managerFirstName\": \"Tom\", " + 
    "\"managerLastName\": \"Litherland\", \"managerId\": \""+testManagerId+"\"," + 
    " \"teamName\": \"Klopps n Robbers\" }";

    static

    

    public void publishMessage(String testMessage) {
        
            ProducerRecord<String, String> testManagerRecord = new ProducerRecord<String, String>(ManagerProducer.MANAGER_TOPIC, testMessage);

            ManagerProducer.sendMessage(testManagerRecord);
    }

    @Test
    public void testReceiveMessage() {

        assertFalse(databaseHelper.doesManagerRecordExist(testManagerId));

        publishMessage(testManagerJson);
        CountDownLatch latch = new CountDownLatch(1);
        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        
        managerConsumer.pollManagerConsumer(Duration.ofMillis(10000));

        managerConsumer.closeConsumer();

        assertTrue(databaseHelper.doesManagerRecordExist(testManagerId));
  
    }

    @Test
    public void testReceiveMessageScale() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        new Thread(managerConsumer).start();

        Integer scale = 1501;

        assertFalse(databaseHelper.doesManagerRecordExist(testManagerId));


        for (int x=1; x<=scale; x++) {
            publishMessage(getManagerJSON("100"+String.valueOf(x)));
        }
        ManagerProducer.closeProducer();


        Runtime.getRuntime().addShutdownHook(new Thread(new ManagerConsumerCloser(managerConsumer)));
        latch.await(10, TimeUnit.SECONDS);

        Integer value = databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable);
        assertEquals(scale, value);
    }

    @Test
    public void testReceiveMessageHugeScale() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        new Thread(managerConsumer).start();

        Integer scale = 15000;

        assertFalse(databaseHelper.doesManagerRecordExist(testManagerId));


        for (int x=1; x<=scale; x++) {
            publishMessage(getManagerJSON("100"+String.valueOf(x)));
        }
        ManagerProducer.closeProducer();
        Runtime.getRuntime().addShutdownHook(new Thread(new ManagerConsumerCloser(managerConsumer)));
        latch.await(20, TimeUnit.SECONDS);

        Integer value = databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable);
        assertEquals(scale, value);
    }

    @Test
    public void testInvalidName() throws InterruptedException {


        CountDownLatch latch = new CountDownLatch(1);
        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        new Thread(managerConsumer).start();

        Integer managerId = 383645;


        FplManagerFactory managerFactory = new FplManagerFactory();
        logger.info("Gets here");
        managerFactory.createFplManager(managerId);

        latch.await(10, TimeUnit.SECONDS);
        assertTrue(true);


   
    }

    public String getManagerJSON(String managerId) {

        String testManagerJson = "{ \"managerFirstName\": \"Tom\", " + 
            "\"managerLastName\": \"Litherland\", \"managerId\": \""+managerId+"\"," + 
            " \"teamName\": \"Klopps n Robbers\" }";

        return testManagerJson;
    }
    
}
