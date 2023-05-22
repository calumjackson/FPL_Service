package com.fplService.httpConnector;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fplService.httpService.HttpConnector;
import okhttp3.Response;

public class TestHttpConnector {

    static Logger logger;

    private static final String GET_URL = "https://fantasy.premierleague.com/api/entry/999927/";
//    private static final String GET_URL = "https://fantasy.premierleague.com/api/bootstrap-static/";

    @Test
    public void sendGetRequest() {

        logger = LoggerFactory.getLogger(TestHttpConnector.class);
        try {
            assertTrue(sendGET());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    
    private static boolean sendGET() throws IOException {
        HttpConnector connector = new HttpConnector();
        Response response = connector.generateBasicApiRequest(GET_URL);
        int responseCode = response.code();
        logger.info("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            return true;
        } 
        return false; 
     
    }

}
