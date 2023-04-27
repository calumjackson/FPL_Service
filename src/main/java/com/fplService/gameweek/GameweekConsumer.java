package com.fplService.gameweek;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public final class GameweekConsumer implements Runnable {

    public static final String GAMEWEEK_TOPIC = "fpl_gameweeks";
    private KafkaConsumer<String, String> gamweekConsumer;
    private CountDownLatch latch;

    public GameweekConsumer(CountDownLatch latch) {
        createTeamConsumer();
        this.latch = latch;
    }

    public void createTeamConsumer() {

        String boostrapServers = "127.0.0.1:9092";

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

        ;

        Duration timeout = Duration.ofMillis(100);
        try {

            while (true) {

                pollManagerConsumer(timeout);
            }
        } catch (WakeupException e) {
            System.out.println("Woken up");
        } finally {
            gamweekConsumer.close();
            latch.countDown();
        }

    }

    void pollManagerConsumer(Duration timeout) {
        ConsumerRecords<String, String> records = gamweekConsumer.poll(timeout);

        for (ConsumerRecord<String, String> record : records) {
            System.out.printf("topic = %s, partition = %d, offset = %d, " +
                    "customer = %s, country = %s\n",
                    record.topic(), record.partition(), record.offset(),
                    record.key(), record.value());

            
            new FplGameweekDbConnector().storeGameweekFromJSON(record.value());

        }
    }

    public void closeProducer() {
        gamweekConsumer.close();
    }

    @Override
    public void run() {
        receiveMessage();
    }

    public void shutdown() throws InterruptedException {
        gamweekConsumer.wakeup();
        latch.await();
    }

}
