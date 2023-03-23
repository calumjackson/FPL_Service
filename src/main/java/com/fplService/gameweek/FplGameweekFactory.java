package com.fplService.gameweek;

import java.sql.SQLException;
import java.util.List;

import com.fplService.httpService.HttpConnector;

public class FplGameweekFactory {

    public void createManagerGameweek(Integer managerId) {
        
        List<FplGameweek> fplGameweeks = requestGameweekDetails(managerId);
        for (FplGameweek gameweek : fplGameweeks) {

            try {
                // Store in the database.
                new FplGameweekDbConnector().storeGameweek(gameweek);
            } catch (SQLException e) {
                if (e.getErrorCode()== 0) {
                    // e.printStackTrace();
                } else { e.printStackTrace();} 
                 ;
            }
        }
            
    }

    private List<FplGameweek> requestGameweekDetails(Integer managerId) {
        HttpConnector okhttpConnector = new HttpConnector();
        List<FplGameweek> fplGameweeks = okhttpConnector.getGameweekDetailsFromFPL(managerId);
        return fplGameweeks;
    }
    
}
