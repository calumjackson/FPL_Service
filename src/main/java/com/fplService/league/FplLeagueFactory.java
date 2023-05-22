package com.fplService.league;

import com.fplService.httpService.HttpConnector;


public class FplLeagueFactory {

    
    public FplLeague requestFplLeagueDetails(Integer leagueId) {
        HttpConnector okhttpConnector = new HttpConnector();
        FplLeague fplLeague = okhttpConnector.getLeagueDetailsFromFpl(leagueId);
        return fplLeague;
    }

    public FplLeague requestFplLeagueDetails(Integer leagueId, Integer maxLeagueSize) {
        HttpConnector okhttpConnector = new HttpConnector();
        okhttpConnector.setMaxLeagueSize(maxLeagueSize);
        FplLeague fplLeague = okhttpConnector.getLeagueDetailsFromFpl(leagueId);
        return fplLeague;
    }


}
