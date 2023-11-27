package com.fplService.manager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.config.ConfigFile;
import com.fplService.gameweek.FplGameweekFactory;
import com.google.gson.Gson;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public final class ManagerGameweekGenerator implements Runnable {

    public static final String MANAGER_TOPIC = "fpl_managers";
    public static final String GAMEWEEK_GEN_GROUP = "gameweek_generator_group";

    private KafkaConsumer<String, String> managerConsumer;
    private CountDownLatch latch;
    Logger logger;
    private static Integer TIMEOUT_LENGTH = 5000;

    public ManagerGameweekGenerator(CountDownLatch latch) {
        this.latch = latch;
        logger = LoggerFactory.getLogger(ManagerGameweekGenerator.class);
    }

    public void createManagerGameweekConsumer() {

        // String boostrapServers = "127.0.0.1:9092";
        String boostrapServers = ConfigFile.HOSTIP+":9092";

        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GAMEWEEK_GEN_GROUP);
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerProps);

        this.managerConsumer = consumer;
        
        managerConsumer.subscribe(Collections.singletonList(MANAGER_TOPIC));
    }

    public void receiveMessage() {

        Duration timeout = Duration.ofMillis(TIMEOUT_LENGTH);
        try {

            while (true) {
                pollManagerConsumer(timeout);                    
                
            }
        } catch (WakeupException e) {
            System.out.println("Manager Gameweek Consumer Woken up");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            managerConsumer.commitAsync();
            managerConsumer.close();
            latch.countDown();
        }

    }

    void pollManagerConsumer(Duration timeout) {

        Logger logger = LoggerFactory.getLogger(ManagerGameweekGenerator.class);
        List<FplManager> fplManagerList = new ArrayList<FplManager>();
        FplGameweekFactory fplGameweekFactory = new FplGameweekFactory();
        
        logger.debug("Logging gameweeks to generate");
        ConsumerRecords<String, String> records = managerConsumer.poll(timeout);
        
        logger.debug("Number of managers in gameweek poll " + records.count());
        if (records.count() != 0) {

            for (ConsumerRecord<String, String> record : records) {
                
                logger.debug("topic = %s, partition = %d, offset = %d, " +
                "customer = %s, country = %s\n",
                record.topic(), record.partition(), record.offset(),
                record.key(), record.value());

                try {
                    FplManager manager = new Gson().fromJson(record.value(), FplManager.class);
                    fplManagerList.add(manager);
                } catch (Exception e) {
                    logger.info("GSON error:" + e.getMessage());
                    logger.info("GSON error:" + record.value());
                    // throw e;
                }
            }
            for (FplManager manager : fplManagerList) {
                logger.debug("Adding managers");
                fplGameweekFactory.createManagerGameweek(manager.getManagerId());
            }
            
        }
    }

    public void closeConsumer() {
        managerConsumer.close();
    }

    @Override
    public void run() {
        createManagerGameweekConsumer();
        receiveMessage();
    }

    public void shutdown() throws InterruptedException {
        managerConsumer.wakeup();
        latch.await(3,  TimeUnit.SECONDS);
    }

}
