package com.fplService.gameweek;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.fplService.httpService.HttpConnector;

public class FplGameweekFactory {

    public void createManagerGameweek(Integer managerId) {

        GameweekProducer gameweekProducer = new GameweekProducer();
        
        List<FplGameweek> fplGameweeks = requestGameweekDetails(managerId);
            for (FplGameweek gameweek : fplGameweeks) {
                
                ProducerRecord<String, String> gameweekRecord 
                    = new ProducerRecord<String,String>(GameweekProducer.GAMEWEEK_TOPIC, gameweek.toString());

                gameweekProducer.sendMessage(gameweekRecord);
                
                // new FplGameweekDbConnector().storeGameweek(gameweek);

        }

        gameweekProducer.closeProducer();
            
    }

    private List<FplGameweek> requestGameweekDetails(Integer managerId) {
        HttpConnector okhttpConnector = new HttpConnector();
        List<FplGameweek> fplGameweeks = okhttpConnector.getGameweekDetailsFromFPL(managerId);
        return fplGameweeks;
    }
    
}
