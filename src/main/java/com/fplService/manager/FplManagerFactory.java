package com.fplService.manager;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.fplService.httpService.HttpConnector;
import com.fplService.managerDatabase.FplManagerDBFactory;


public class FplManagerFactory {

    public void createFplManager(Integer managerId) {
        
        FplManager fplManager = requestFplManagerDetails(managerId);

        try {
            // Store in the database.
            new FplManagerDBFactory().storeManager(fplManager);
            
            ManagerProducer managerProducer = new ManagerProducer();
            managerProducer.createTeamProducer();

            String managerMessage = fplManager.getManagerId().toString() + ": " + fplManager.getManagerFirstName() + " " + fplManager.getManagerLastName();

            ProducerRecord<String, String> managerRecord = new ProducerRecord<String, String>("test", managerMessage);
            managerProducer.sendMessage(managerRecord);

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
