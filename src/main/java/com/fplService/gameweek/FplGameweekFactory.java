package com.fplService.gameweek;

import java.util.List;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.httpService.HttpConnector;

public class FplGameweekFactory {

    private Integer gameweekNumber = 36;

    public boolean createManagerGameweek(Integer managerId) {
        Logger logger = LoggerFactory.getLogger(FplGameweekFactory.class);
        List<FplGameweek> fplGameweeks = requestGameweekDetails(managerId);
        if (fplGameweeks.size() < 36) {
            // logger.debug("Manager only has " + fplGameweeks.size() + " gameweeks - excluded");
            return false;
        }
        for (FplGameweek gameweek : fplGameweeks) {
            
            ProducerRecord<String, String> gameweekRecord 
                = new ProducerRecord<String,String>(GameweekProducer.GAMEWEEK_TOPIC, gameweek.toString());

            GameweekProducer.sendMessage(gameweekRecord);
        }            
        return true;
    }

    private List<FplGameweek> requestGameweekDetails(Integer managerId) {
        HttpConnector okhttpConnector = new HttpConnector();
        List<FplGameweek> fplGameweeks = okhttpConnector.getGameweekDetailsFromFPL(managerId);
        return fplGameweeks;
    }
    
    public Integer getGameweekNumber() {
        return gameweekNumber;
    }

    public void setGameweekNumber(Integer gameweekNumber) {
        this.gameweekNumber = gameweekNumber;
    }

}
