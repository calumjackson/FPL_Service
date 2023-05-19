package com.fplService.httpService;
import com.fplService.gameweek.FplGameweek;
import com.fplService.gameweek.FplGameweekResponseDecoder;
import com.fplService.league.FplLeague;
import com.fplService.league.LeagueResponseDecoder;
import com.fplService.manager.FplManager;
import com.fplService.manager.ManagerDetailsResponseDecoder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpConnector {

    ObjectMapper mapper = new ObjectMapper();

    OkHttpClient client = new OkHttpClient();

    public void generateApiRequest(Integer userId) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "keep-alive");

        String fplUrl = "https://fantasy.premierleague.com/api/entry/"+userId+"/history/";

        
        Request request = getRequest(fplUrl);

        Response response = null;
        try {
            response = client.newCall(request).execute();

            ResponseBody responseBody = response.body();
            String managerJsonString = responseBody.string();
            // System.out.println(managerJsonString);
            new ManagerDetailsResponseDecoder().decodeResponse(managerJsonString);

            System.out.println(response.isSuccessful());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FplManager getManagerDetailsFromFPL(Integer userId) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "keep-alive");

        String fplUrl = "https://fantasy.premierleague.com/api/entry/"+userId+"/";

        Request request = getRequest(fplUrl);

        Response response = null;
        try {
            response = client.newCall(request).execute();

            String managerJsonString = response.body().string();
            return new ManagerDetailsResponseDecoder().decodeResponse(managerJsonString);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FplLeague getLeagueDetailsFromFpl(Integer leagueId) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "keep-alive");
        Response response = null;
        Integer pageStanding = 2945;
        Boolean hasNext=true;
        
        
        LeagueResponseDecoder leagueResponseDecoder = new LeagueResponseDecoder();
        
        while (hasNext) {
            Request request = getRequest(generateFplLeagueRequestUrl(leagueId, pageStanding));
            // System.out.println("Has next : " + hasNext + " & page Standing " + pageStanding);
            try {
                response = client.newCall(request).execute();
                String managerJsonString = response.body().string(); 
                hasNext = leagueResponseDecoder.decodeResponses(managerJsonString);
                response.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            pageStanding++;
            if (pageStanding%50 == 0) {
                System.out.println("On page : " + pageStanding);
            }
        }
        
        FplLeague fplLeague = new FplLeague(leagueId, leagueResponseDecoder.getManagerIds());
        return fplLeague;
    }
        
    public String generateFplLeagueRequestUrl(Integer leagueId, Integer pageStanding) {
        
        String fplUrl = "https://fantasy.premierleague.com/api/leagues-classic/"+leagueId+"/standings/?page_standings="+pageStanding;
        return fplUrl;
    }


    public List<FplGameweek> getGameweekDetailsFromFPL(Integer managerId) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Connection", "keep-alive");

        String fplUrl = "https://fantasy.premierleague.com/api/entry/"+managerId+"/history/";

        Request request = getRequest(fplUrl);

        Response response = null;
        try {
            response = client.newCall(request).execute();

            String managerJsonString = response.body().string();
            
            List<FplGameweek> gameweeks = new FplGameweekResponseDecoder().decodeResponse(managerJsonString);
            for (FplGameweek gameweek : gameweeks) {
                gameweek.setSeasonId("2022-23");
                gameweek.setManagerId(managerId);
                
            }

            return gameweeks;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }    

    @NotNull
    private static Request getRequest(String fplUrl) {

        Request request = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .header("Host", "fantasy.premierleague.com")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Accept", "*/*")
                .url(fplUrl)
                .build(); // defaults to GET

        return request;
    }

    

}
