package com.fplService.player;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.bootstrap.FPLGameweekNumberDecoder;
import com.fplService.httpConnector.TestHttpConnector;
import com.fplService.httpService.HttpConnector;
import com.fplService.playerStats.FplPlayerDBUtil;
import com.fplService.playerStats.FplPlayerDecoder;
import com.fplService.playerStats.FplPlayerList;

public class FplTransferHistoriesTest {
    
    static Logger logger;

    @BeforeClass
    public static void burnDownPlayerList() {
        try {
            // new FplPlayerDBUtil().de();
        logger = LoggerFactory.getLogger(FplTransferHistoriesTest.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void decodeBootstrapInfoTransHist() {

        FplPlayerDecoder decoder = new FplPlayerDecoder();
        HttpConnector connector = new HttpConnector();

        try {
            String playerListString = connector.getBootstrap();
            FplPlayerList playerList = decoder.decodeResponse(playerListString);
            
            for (Integer x = 0; x<30; x++) {
                System.out.println(playerList.getElements()[x].getId() + " " + playerList.getElements()[x].getTransfers_in() + " " + playerList.getElements()[x].getTransfers_in_event());
            }

            assertTrue(playerList.getElements().length>0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void decodeAndCommitTransferHistoryCounts() {

        FplPlayerDecoder decoder = new FplPlayerDecoder();
        HttpConnector connector = new HttpConnector();
        FplPlayerList playerList = null;

        logger = LoggerFactory.getLogger(TestHttpConnector.class);
        try {
            String playerListString = connector.getBootstrap();
            playerList = decoder.decodeResponse(playerListString);
            
            for (Integer x = 0; x<10; x++) {
                System.out.println(playerList.getElements()[x].getFirst_name() + " " + playerList.getElements()[x].getSecond_name() + " " + playerList.getElements()[x].getTransfers_in());
            }

            FplPlayerDBUtil dbUtil = new FplPlayerDBUtil();
            dbUtil.batchStoreTransferHistories(playerList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertTrue(playerList.getElements().length>0);

    }

    @Test
    public void testGetGameweek() {

        FPLGameweekNumberDecoder decoder = new FPLGameweekNumberDecoder();

        System.out.println(decoder.getCurrentGameweekId());
            
        assertTrue(true);

    }


    
}
