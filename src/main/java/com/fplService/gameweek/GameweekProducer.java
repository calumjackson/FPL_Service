package com.fplService.gameweek;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class GameweekProducer {
    
    private KafkaProducer<String, String> gameweekProducer;
    static String GAMEWEEK_TOPIC = "test"; 


    public GameweekProducer() {
        createGameweekProducer();
    }

    public void createGameweekProducer() {

        String boostrapServers = "localhost:9092"; 
    
            Properties producerProps = new Properties();
            producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
            producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    
            KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);
    
            this.gameweekProducer = producer;
    }

    public void sendMessage(ProducerRecord<String, String> producerRecord) {

        gameweekProducer.send(producerRecord);

    }  

    public void closeProducer() {
        gameweekProducer.close();
    }

}
