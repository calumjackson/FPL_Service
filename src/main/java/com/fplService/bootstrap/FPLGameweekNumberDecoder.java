package com.fplService.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.httpService.HttpConnector;
import com.google.gson.Gson;

public class FPLGameweekNumberDecoder {
 
    Logger logger;
    
    public FPLGameweekList decodeResponse(String responseBody) {
        logger = LoggerFactory.getLogger(FPLGameweekNumberDecoder.class);

        FPLGameweekList gameweekList = null;
        gameweekList = new Gson().fromJson((responseBody), FPLGameweekList.class);
        return gameweekList;
        
    }

    public Integer getCurrentGameweekId() {

        HttpConnector okhttpConnector = new HttpConnector();
        String fplGameweeks = okhttpConnector.getBootstrap();
        FPLGameweekList gameweekList = decodeResponse(fplGameweeks);
        
        Integer gameweekId = 0;
        for (FPLGameweek gameweek : gameweekList.getEvents()) {
            if (gameweek.is_current == "true") {
                gameweekId = gameweek.getId();
            }
        }
        return gameweekId;
    }
    
    public class FPLGameweekList {
    
        FPLGameweek[] events;
    
            public void events(FPLGameweek[] events) {
                this.events = events;
            }
    
            public FPLGameweek[] getEvents() {
                return events;
            }        
        
    }

    public class FPLGameweek {

        Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        String is_current;

        public String getIs_current() {
            return is_current;
        }

        public void setIs_current(String is_current) {
            this.is_current = is_current;
        }

        
    }

}
