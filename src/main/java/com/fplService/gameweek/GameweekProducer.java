package com.fplService.gameweek;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.config.ConfigFile;

public class GameweekProducer {
    
    private static KafkaProducer<String, String> gameweekProducer;
    static String GAMEWEEK_TOPIC = "fpl_gameweeks"; 
    static Logger logger;

    public GameweekProducer() {
        createGameweekProducer();
    }

    public static void createGameweekProducer() {

        logger = LoggerFactory.getLogger(GameweekProducer.class);
    
        // String boostrapServers = "localhost:9092"; 
        String boostrapServers = ConfigFile.HOSTIP+":9092";

    
            Properties producerProps = new Properties();
            producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
            producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProps.setProperty(ProducerConfig.LINGER_MS_CONFIG, "500");

            KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);
    
            GameweekProducer.gameweekProducer = producer;
            logger.debug("Setup: Producer added");
    }

    public static void sendMessage(ProducerRecord<String, String> producerRecord) {

        try {
            gameweekProducer.send(producerRecord);
        } catch (Exception e) {
            logger.info("Issue sending record: " + producerRecord.value());
            e.printStackTrace();
        }

    }  
    
    public static void closeProducer() {
        gameweekProducer.flush();
        gameweekProducer.close();
        logger.info("Gameweek Producer Closed");

    }

}
