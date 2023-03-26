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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpConnector {

    ObjectMapper mapper = new ObjectMapper();

    OkHttpClient client = new OkHttpClient();


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

        String fplUrl = "https://fantasy.premierleague.com/api/leagues-classic/"+leagueId+"/standings/";

        Request request = getRequest(fplUrl);

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String managerJsonString = response.body().string();
            return new FplLeague(leagueId, new LeagueResponseDecoder().decodeResponse(managerJsonString));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
