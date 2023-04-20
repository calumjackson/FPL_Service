package com.fplService.team_producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class team_producer {
    
    public KafkaProducer<String, String> initiateTeamProducer() {

    String boostrapServers = "localhost:9092"; 

        Properties producerProps = new Properties();
        producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        return producer;
    }

    private void extracted(KafkaProducer<String, String> producer) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<String,String>("test", "hello world part 2");
        
        producer.send(producerRecord);
        
        producer.close();
    }

}
