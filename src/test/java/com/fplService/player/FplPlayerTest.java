package com.fplService.player;

import static org.junit.Assert.assertTrue;


import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fplService.bootstrap.FplTeamDBUtil;
import com.fplService.bootstrap.FplTeamDecoder;
import com.fplService.bootstrap.FplTeamList;
import com.fplService.httpConnector.TestHttpConnector;
import com.fplService.httpService.HttpConnector;
import com.fplService.playerStats.FplPlayerDBUtil;
import com.fplService.playerStats.FplPlayerDecoder;
import com.fplService.playerStats.FplPlayerList;

public class FplPlayerTest {
    
    Logger logger;

    @BeforeClass
    public static void burnDownPlayerList() {
        try {
            new FplPlayerDBUtil().deleteAllPlayers();
            new FplTeamDBUtil().deleteAllTeams();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void retrieveBootstrapInfo() {

        HttpConnector connector = new HttpConnector();

        logger = LoggerFactory.getLogger(FplPlayerTest.class);
        try {
            String playerListString = connector.getBootstrap();
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
            String playerListString = connector.getBootstrap();
            FplPlayerList playerList = decoder.decodeResponse(playerListString);
            
            for (Integer x = 0; x<30; x++) {
                System.out.println(playerList.getElements()[x].getFirst_name() + " " + playerList.getElements()[x].getSecond_name());
            }

            assertTrue(playerList.getElements().length>0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void decodeAndCommitBootstrapInfo() {

        FplPlayerDecoder decoder = new FplPlayerDecoder();
        HttpConnector connector = new HttpConnector();
        FplPlayerList playerList = null;

        logger = LoggerFactory.getLogger(TestHttpConnector.class);
        try {
            String playerListString = connector.getBootstrap();
            playerList = decoder.decodeResponse(playerListString);
            
            for (Integer x = 0; x<30; x++) {
                System.out.println(playerList.getElements()[x].getFirst_name() + " " + playerList.getElements()[x].getSecond_name());
            }

            FplPlayerDBUtil dbUtil = new FplPlayerDBUtil();
            dbUtil.batchStorePlayers(playerList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertTrue(playerList.getElements().length>0);

    }


    @Test
    public void decodeAndCommitBootstrapTeams() {

        FplTeamDecoder decoder = new FplTeamDecoder();
        HttpConnector connector = new HttpConnector();
        FplTeamList teamList = null;

        logger = LoggerFactory.getLogger(TestHttpConnector.class);
        try {
            String teamListString = connector.getBootstrap();
            teamList = decoder.decodeResponse(teamListString);
            
            for (Integer x = 0; x<20; x++) {
                System.out.println(teamList.getTeams()[x].getName());
            }

            FplTeamDBUtil dbUtil = new FplTeamDBUtil();
            dbUtil.batchStoreTeams(teamList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertTrue(teamList.getTeams().length>0);

    }


    @Test
    public void showEpochTimestamp() {
        Boolean output;
        output = true;
        Long timestamp = System.currentTimeMillis();
        System.out.println(System.currentTimeMillis());
        System.out.println(timestamp.toString());
        assertTrue(output);
    }


}
