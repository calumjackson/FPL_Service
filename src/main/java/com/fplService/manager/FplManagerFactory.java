package com.fplService.manager;

import org.apache.kafka.clients.producer.ProducerRecord;
import com.fplService.httpService.HttpConnector;



public class FplManagerFactory {

    public void createFplManager(Integer managerId) {
        
        FplManager fplManager = requestFplManagerDetails(managerId);

        try {
            
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
