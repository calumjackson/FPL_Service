package com.fplService.manager;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ManagerProducer {

    public static final String MANAGER_TOPIC = "fpl_managers";
    private KafkaProducer<String, String> managerProducer;

    public ManagerProducer() {
        createTeamProducer();
    }

    public void createTeamProducer() {

        String boostrapServers = "localhost:9092"; 
    
            Properties producerProps = new Properties();
            producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
            producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    
            KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);
    
            this.managerProducer = producer;
    }

    public void sendMessage(ProducerRecord<String, String> producerRecord) {

        managerProducer.send(producerRecord);        

    }  
    
    public void closeProducer() {
        managerProducer.flush();
        managerProducer.close();
    }
}
