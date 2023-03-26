package com.fplService.manager;

import com.fplService.httpService.HttpConnector;
import com.fplService.managerDatabase.FplManagerDBFactory;


public class FplManagerFactory {

    public void createFplManager(Integer managerId) {
        
        FplManager fplManager = requestFplManagerDetails(managerId);

        try {
            // Store in the database.
            new FplManagerDBFactory().storeManager(fplManager);

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
