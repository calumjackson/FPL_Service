package com.fplService.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.httpService.HttpConnector;
import com.google.gson.Gson;

public class FplTeamDecoder {
 
    Logger logger;
    
    public FplTeamList decodeResponse(String responseBody) {
        logger = LoggerFactory.getLogger(FplTeamDecoder.class);

        FplTeamList teamList = null;
        teamList = new Gson().fromJson((responseBody), FplTeamList.class);
        
        return teamList;
    }

    
    public FplTeamList requestPlayerBootstrap() {

        HttpConnector okhttpConnector = new HttpConnector();
        String fplTeams = okhttpConnector.getBootstrap();
        FplTeamList fplTeamList = decodeResponse(fplTeams);
        return fplTeamList;
    }
    
    

}
