package com.fplService.gameweek;

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

import com.google.gson.Gson;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public final class GameweekConsumer implements Runnable {

    public static final String GAMEWEEK_TOPIC = "fpl_gameweeks";
    private KafkaConsumer<String, String> gamweekConsumer;
    private CountDownLatch latch;
    private static Integer TIMEOUT_LENGTH = 5000;


    public GameweekConsumer(CountDownLatch latch) {
        
        createTeamConsumer();
        this.latch = latch;
    }

    public void createTeamConsumer() {

        String boostrapServers = "13.40.213.178:9092";
        // String boostrapServers = "127.0.0.1:9092";

        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GAMEWEEK_TOPIC);
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerProps);

        this.gamweekConsumer = consumer;
        
        gamweekConsumer.subscribe(Collections.singletonList(GAMEWEEK_TOPIC));
    }

    public void receiveMessage() {

        Duration timeout = Duration.ofMillis(TIMEOUT_LENGTH);

        try {

            while (true) {

                pollGameweekConsumer(timeout);
            }
        } catch (WakeupException e) {
            System.out.println("Gameweek Consumer Woken up");
        } finally {
            gamweekConsumer.commitAsync();
            gamweekConsumer.close();
            latch.countDown();
        }

    }

    void pollGameweekConsumer(Duration timeout) {

        Logger logger = LoggerFactory.getLogger(GameweekConsumer.class);
        List<FplGameweek> fplGameweekList = new ArrayList<>();

        logger.debug("Starting to log");
        ConsumerRecords<String, String> records = gamweekConsumer.poll(timeout);
        
        logger.info("Number of gameweeks in poll :" + records.count());
        
        if (records.count() != 0) { 

            for (ConsumerRecord<String, String> record : records) {
                logger.debug("topic = %s, partition = %d, offset = %d, " +
                "customer = %s, country = %s\n",
                record.topic(), record.partition(), record.offset(),
                record.key(), record.value());
                FplGameweek fplgameweek = new Gson().fromJson((record.value()), FplGameweek.class);
                fplGameweekList.add(fplgameweek);
            }
            
            new FplGameweekDbConnector().batchStoreGameweeks(fplGameweekList);
        }
        
    }

    public void closeConsumer() {
        gamweekConsumer.close();
    }

    @Override
    public void run() {
        receiveMessage();
    }

    public void shutdown() throws InterruptedException {
        gamweekConsumer.wakeup();
        latch.await(4, TimeUnit.SECONDS);   
     }

}
    