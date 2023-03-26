package com.fplService.league;

import com.fplService.httpService.HttpConnector;


public class FplLeagueFactory {

    
    public FplLeague createFplLeage(Integer leagueId) {
        return requestFplLeagueDetails(leagueId);
    }

    private FplLeague requestFplLeagueDetails(Integer leagueId) {
        HttpConnector okhttpConnector = new HttpConnector();
        FplLeague fplLeague = okhttpConnector.getLeagueDetailsFromFpl(leagueId);
        return fplLeague;
    }


}
