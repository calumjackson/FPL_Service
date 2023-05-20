package com.fplService.manager;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.httpService.HttpConnector;

public class FplManagerFactory {

    Logger logger;

    public void createFplManager(Integer managerId) {
        logger = LoggerFactory.getLogger(FplManager.class);
        FplManager fplManager = requestFplManagerDetails(managerId);

        try {
            logger.debug("Creating manager " + managerId);
            ProducerRecord<String, String> managerRecord = new ProducerRecord<String, String>(ManagerProducer.MANAGER_TOPIC, fplManager.toString());
            ManagerProducer.sendMessage(managerRecord);
            

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private FplManager requestFplManagerDetails(Integer managerId) {
        
        HttpConnector okhttpConnector = new HttpConnector();
        FplManager fplManager = okhttpConnector.getManagerDetailsFromFPL(managerId);
        return fplManager;
    }
    

}
