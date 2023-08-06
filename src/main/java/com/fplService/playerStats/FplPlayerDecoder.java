package com.fplService.playerStats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.httpService.HttpConnector;
import com.google.gson.Gson;

public class FplPlayerDecoder {
 
    Logger logger;
    
    public FplPlayerList decodeResponse(String responseBody) {
        logger = LoggerFactory.getLogger(FplPlayerDecoder.class);

        FplPlayerList playerList = null;
        playerList = new Gson().fromJson((responseBody), FplPlayerList.class);
        
        return playerList;
    }

    
    public FplPlayerList requestPlayerBootstrap() {

        HttpConnector okhttpConnector = new HttpConnector();
        String fplPlayers = okhttpConnector.getBootstrap();
        FplPlayerList fplPlayersList = decodeResponse(fplPlayers);
        return fplPlayersList;
    }
    
    

}
