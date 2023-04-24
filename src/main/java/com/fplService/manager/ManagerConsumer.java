package com.fplService.manager;

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

public final class ManagerConsumer implements Runnable {

    public static final String MANAGER_TOPIC = "test";
    private KafkaConsumer<String, String> managerConsumer;
    private CountDownLatch latch;

    public ManagerConsumer(CountDownLatch latch) {
        createTeamConsumer();
        this.latch = latch;
    }

    public void createTeamConsumer() {

        String boostrapServers = "127.0.0.1:9092";

        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, MANAGER_TOPIC);
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerProps);

        this.managerConsumer = consumer;

    }

    public void receiveMessage() {

        managerConsumer.subscribe(Collections.singletonList(MANAGER_TOPIC));
        ;

        Duration timeout = Duration.ofMillis(100);
        try {

            while (true) {

                ConsumerRecords<String, String> records = managerConsumer.poll(timeout);

                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("topic = %s, partition = %d, offset = %d, " +
                            "customer = %s, country = %s\n",
                            record.topic(), record.partition(), record.offset(),
                            record.key(), record.value());

                }
            }
        } catch (WakeupException e) {
            System.out.println("Woken up");
        } finally {
            managerConsumer.close();
            latch.countDown();
        }

    }

    public void closeProducer() {
        managerConsumer.close();
    }

    @Override
    public void run() {
        receiveMessage();
    }

    public void shutdown() throws InterruptedException {
        managerConsumer.wakeup();
        System.out.println("gets here");
        latch.await();
    }

}
