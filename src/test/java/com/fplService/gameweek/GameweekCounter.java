package com.fplService.gameweek;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameweekCounter implements Runnable {
 
    Logger logger;
    CountDownLatch latch;
    static String GAMEWEEK_TOPIC = "fpl_gameweeks"; 
    private KafkaConsumer<String, String> gameweekConsumer;
    private static Integer TIMEOUT_LENGTH = 5000;

    private static Integer gameweekCounter = 0;

    
    public GameweekCounter(CountDownLatch latch) {
        this.latch = latch;
        logger = LoggerFactory.getLogger(GameweekCounter.class);
    }

    public void startGameweekCounterConsumer() {

        String boostrapServers = "127.0.0.1:9092";

        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GAMEWEEK_TOPIC);
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "GameweekCounter");

        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerProps);

        this.gameweekConsumer = consumer;
        
        gameweekConsumer.subscribe(Collections.singletonList(GAMEWEEK_TOPIC));
    }

    public void receiveMessage() {

        Duration timeout = Duration.ofMillis(TIMEOUT_LENGTH);
        try {

            while (true) {

                pollManagerConsumer(timeout);
                
            }
        } catch (WakeupException e) {
            System.out.println("Gameweek Counter Woken up");
        } finally {
            gameweekConsumer.commitAsync();
            gameweekConsumer.close();
            latch.countDown();
        }

    }

    void pollManagerConsumer(Duration timeout) {

        Logger logger = LoggerFactory.getLogger(GameweekCounter.class);
        ConsumerRecords<String, String> records = gameweekConsumer.poll(timeout);
        gameweekCounter += records.count();        
        
    }

    public void closeConsumer() {
        gameweekConsumer.close();
    }

    @Override
    public void run() {
        startGameweekCounterConsumer();
        receiveMessage();
    }

    public void shutdown() throws InterruptedException {
        gameweekConsumer.wakeup();
        latch.await(3,  TimeUnit.SECONDS);
    }

    public static Integer getGameweekCounter() {
        return gameweekCounter;
    }

    public static void setGameweekCounter(Integer gameweekCounter) {
        GameweekCounter.gameweekCounter = gameweekCounter;
    }

    public static void resetGameweekCounter() {
        GameweekCounter.gameweekCounter = 0;
    }


}
