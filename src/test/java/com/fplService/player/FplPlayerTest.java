package com.fplService.player;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.httpConnector.TestHttpConnector;
import com.fplService.httpService.HttpConnector;
import com.fplService.playerStats.FplPlayer;
import com.fplService.playerStats.FplPlayerDecoder;
import com.fplService.playerStats.FplPlayerList;

public class FplPlayerTest {
    
    Logger logger;

    @Test
    public void retrieveBootstrapInfo() {

        HttpConnector connector = new HttpConnector();

        logger = LoggerFactory.getLogger(TestHttpConnector.class);
        try {
            String playerListString = connector.getPlayerBootstrap();
            System.out.println(playerListString);
            // decoder.decodeResponse(null);

            assertTrue(playerListString.length()>10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void decodeBootstrapInfo() {

        FplPlayerDecoder decoder = new FplPlayerDecoder();
        HttpConnector connector = new HttpConnector();

        logger = LoggerFactory.getLogger(TestHttpConnector.class);
        try {
            String playerListString = connector.getPlayerBootstrap();
            FplPlayerList playerList = decoder.decodeResponse(playerListString);
            
            for (Integer x = 0; x<30; x++) {
                System.out.println(playerList.getElements()[x].getFirst_name() + " " + playerList.getElements()[x].getSecond_name());
            }

            assertTrue(playerList.getElements().length>0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
