package com.fplService.playerStats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
