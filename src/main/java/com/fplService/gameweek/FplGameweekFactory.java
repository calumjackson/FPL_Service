package com.fplService.gameweek;

import java.util.List;

import com.fplService.httpService.HttpConnector;

public class FplGameweekFactory {

    public void createManagerGameweek(Integer managerId) {
        
        List<FplGameweek> fplGameweeks = requestGameweekDetails(managerId);
        for (FplGameweek gameweek : fplGameweeks) {

            new FplGameweekDbConnector().storeGameweek(gameweek);

        }
            
    }

    private List<FplGameweek> requestGameweekDetails(Integer managerId) {
        HttpConnector okhttpConnector = new HttpConnector();
        List<FplGameweek> fplGameweeks = okhttpConnector.getGameweekDetailsFromFPL(managerId);
        return fplGameweeks;
    }
    
}
