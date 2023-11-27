package com.fplService.manager;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
import com.fplService.gameweek.FplGameweekFactory;
import com.fplService.gameweek.GameweekConsumer;
import com.fplService.gameweek.GameweekConsumerCloser;
import com.fplService.gameweek.GameweekProducer;
import com.google.gson.Gson;


public class FPLManagerFactoryTest {

    private DatabaseUtilHelper databaseHelper = null;
    Logger logger;
    Integer currentGameweek = new FPLGameweekNumberDecoder().getCurrentGameweekId();

    @Before
    public void setUpTestManager() {

        logger = LoggerFactory.getLogger(FPLManagerFactoryTest.class);
        this.databaseHelper = new DatabaseUtilHelper();
        databaseHelper.startConnections();

        try {
            databaseHelper.deleteAllRecordsFromTable(DatabaseUtilHelper.fplManagersTable);
            databaseHelper.deleteAllRecordsFromTable(DatabaseUtilHelper.fplGameweekTable);

        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
    }

    @BeforeClass 
    public static void setup() {

        new ManagerProducer();
        new GameweekProducer();

        CountDownLatch latch = new CountDownLatch(1);
        
        GameweekConsumer gameweekConsumer = new GameweekConsumer(latch);
        new Thread(gameweekConsumer).start();
        Runtime.getRuntime().addShutdownHook(new Thread(new GameweekConsumerCloser(gameweekConsumer)));
        
        ManagerGameweekGenerator managerGameweekGeneratorConsumer = new ManagerGameweekGenerator(latch);
        new Thread(managerGameweekGeneratorConsumer).start();
        Runtime.getRuntime().addShutdownHook(new Thread(new ManagerGameweekGeneratorCloser(managerGameweekGeneratorConsumer)));

    }

    @After
    public void closeDatabase() throws SQLException {
        databaseHelper.closeConnection();
        // databaseHelper.deleteAllRecordsFromTable(DatabaseUtilHelper.fplManagersTable);

    }
    
    @AfterClass
    public static void tearDown() {

        ManagerProducer.closeProducer();
        GameweekProducer.closeProducer();
    }    

    static String testManagerId = "2046938";
    static String testManagerJson = "{ \"managerFirstName\": \"Tom\", " + 
    "\"managerLastName\": \"Litherland\", \"managerId\": \""+testManagerId+"\"," + 
    " \"teamName\": \"Klopps n Robbers\" }";

    static String testInvalidTeamNameManagerId = "5788418";
    static String testInvalidCharJson = "{ \"managerFirstName\": \"Elgan\", \"managerLastName\": \"Jones\", \"managerId\": \"5788418\", \"teamName\": \"\\o/\" }";
    // static String testInvalidCharJsonWithQuotes = "{ "managerFirstName": "Marko", "managerLastName": "Žubrinić", "managerId": "557148", "teamName": "\"maximuss128\"" };


    public void publishMessage(String testMessage)  {

            // System.out.println(testMessage.replace("\\", "\\\\").replace("/", "\\/"));
            FplManager fplManager = new FplManager(testMessage.replace("\\", "\\\\").replace("/", "\\/"));

            ProducerRecord<String, String> testManagerRecord = new ProducerRecord<String, String>(ManagerProducer.MANAGER_TOPIC, fplManager.toString());

            ManagerProducer.sendMessage(testManagerRecord);
            Integer expected = 0;
            Integer actual = 0;
            assertEquals(expected, actual);
    }

    @Test
    public void testJSONMapping() {
        String testJson = testInvalidCharJson.replace("\\", "\\\\").replace("/", "\\/");
        logger.info("Test json " + testJson);

        FplManager manager = new Gson().fromJson(testJson, FplManager.class);
        logger.info("Manager Info: " + manager.getTeamName());
    }

    @Test
    public void testInvalidTeamName() throws InterruptedException {
        
        assertFalse(databaseHelper.doesManagerRecordExist(testInvalidTeamNameManagerId));
        publishMessage(testInvalidCharJson);
        CountDownLatch latch = new CountDownLatch(1);
        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        managerConsumer.createTeamConsumer();
        managerConsumer.pollManagerConsumer(Duration.ofMillis(10000));

        managerConsumer.closeConsumer();
        latch.await(1, TimeUnit.SECONDS);

        assertTrue(databaseHelper.doesManagerRecordExist(testInvalidTeamNameManagerId));

    }

    @Test
    public void testGetManager() throws InterruptedException {

        Integer managerId = 931833;
        

        //publishMessage(testManagerJson);
        
        new FplManagerFactory().createFplManager(managerId);
        
        Integer gameweekCount = new FplGameweekFactory().getManagerGameweekCount(managerId);

        Thread.sleep(6000);
        Integer expected = currentGameweek;
        assertEquals(expected, gameweekCount);
        assertEquals(expected, databaseHelper.getRecordCount(DatabaseUtilHelper.fplGameweekTable, Integer.toString(managerId)));
  
    }

    @Test
    public void testReceiveMessage() throws InterruptedException {

        assertFalse(databaseHelper.doesManagerRecordExist(testManagerId));
        publishMessage(testManagerJson);
        CountDownLatch latch = new CountDownLatch(1);
        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        managerConsumer.createTeamConsumer();
        managerConsumer.pollManagerConsumer(Duration.ofMillis(10000));

        managerConsumer.closeConsumer();
        latch.await(1, TimeUnit.SECONDS);

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


        Runtime.getRuntime().addShutdownHook(new Thread(new ManagerConsumerCloser(managerConsumer)));
        latch.await(10, TimeUnit.SECONDS);
        Thread.sleep(3000);

        Integer value = databaseHelper.getRecordCount(DatabaseUtilHelper.fplManagersTable);

        assertEquals(scale, value);
    }

    // @Test
    public void testReceiveMessageHugeScale() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        ManagerConsumer managerConsumer = new ManagerConsumer(latch);
        new Thread(managerConsumer).start();

        Integer scale = 15000;

        assertFalse(databaseHelper.doesManagerRecordExist(testManagerId));


        for (int x=1; x<=scale; x++) {
            publishMessage(getManagerJSON("100"+String.valueOf(x)));
        }
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
