package com.fplService.gameweek;

import com.google.gson.Gson;

public class FplGameweek {
    
    Integer managerId;
    Integer gameweekId;
    String seasonId;
    Integer gameweekPoints;
    Integer gameweekBenchPoints;
    Integer transferPointCosts;

    
    public FplGameweek(Integer managerId, Integer gameweekId, String seasonId, Integer gameweekPoints,
            Integer gameweekBenchPoints, Integer transferPointCosts) {
        this.managerId = managerId;
        this.gameweekId = gameweekId;
        this.seasonId = seasonId;
        this.gameweekPoints = gameweekPoints;
        this.gameweekBenchPoints = gameweekBenchPoints;
        this.transferPointCosts = transferPointCosts;
    }
    public Integer getManagerId() {
        return managerId;
    }
    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
    public Integer getGameweekId() {
        return gameweekId;
    }
    public void setGameweekId(Integer gameweekId) {
        this.gameweekId = gameweekId;
    }
    public String getSeasonId() {
        return seasonId;
    }
    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }
    public Integer getGameweekPoints() {
        return gameweekPoints;
    }
    public void setGameweekPoints(Integer gameweekPoints) {
        this.gameweekPoints = gameweekPoints;
    }
    public Integer getGameweekBenchPoints() {
        return gameweekBenchPoints;
    }
    public void setGameweekBenchPoints(Integer gameweekBenchPoints) {
        this.gameweekBenchPoints = gameweekBenchPoints;
    }
    public Integer getTransferPointCosts() {
        return transferPointCosts;
    }
    public void setTransferPointCosts(Integer transferPointCosts) {
        this.transferPointCosts = transferPointCosts;
    }


    public String toString() {


        String formatString = "{ \"managerId\": \"%s\", "
                + "\"gameweekId\": \"%s\", "
                + "\"seasonId\": \"%s\", "
                + "\"gameweekPoints\": \"%s\", "
                + "\"gameweekBenchPoints\": \"%s\", "
                + "\"transferPointCosts\": \"%s\" }";


        return String.format(formatString, getManagerId().toString(), getGameweekId().toString()
                        , getSeasonId()
                        , getGameweekPoints().toString(), getGameweekBenchPoints().toString()
                        , getTransferPointCosts().toString());
    }  

    public FplGameweek decodeGameweekJson(String gameweekJSON) {

        FplGameweek gameweek = new Gson().fromJson((gameweekJSON), FplGameweek.class);        
        return gameweek;
        
    }

}
