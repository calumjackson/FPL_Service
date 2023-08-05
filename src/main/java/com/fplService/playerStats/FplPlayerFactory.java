package com.fplService.playerStats;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.httpService.HttpConnector;
import com.fplService.manager.FplManager;
import com.fplService.manager.FplManagerFactory;


public class FplPlayerFactory {
    
    Logger logger;

    public void createFplPlayer(Integer managerId) {
        logger = LoggerFactory.getLogger(FplManagerFactory.class);
        
        logger.debug("Retrieving player info");

        FplPlayer fplPlayer = new FplPlayer("Calum", "Jackson", 1, 3, 1);
        logger.debug(fplPlayer.getFirst_name());


    }

}
