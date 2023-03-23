package com.fplService.league;

import com.fplService.httpService.HttpConnector;


public class FplLeagueFactory {

    
    public FplLeague createFplLeage(Integer leagueId) {
        
        FplLeague fplLeague = requestFplLeagueDetails(leagueId);

        System.out.println(fplLeague.getLeagueName());
        for (Integer managerId : fplLeague.getManagerIds()) {
            System.out.println(managerId);
        }

        return fplLeague;
    }

    private FplLeague requestFplLeagueDetails(Integer leagueId) {
        HttpConnector okhttpConnector = new HttpConnector();
        FplLeague fplLeague = okhttpConnector.getLeagueDetailsFromFpl(leagueId);
        return fplLeague;
    }


}
