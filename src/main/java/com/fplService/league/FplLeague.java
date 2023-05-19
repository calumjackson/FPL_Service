package com.fplService.league;

import java.util.List;

public class FplLeague {
    
    Integer leagueId;
    String leagueName;
    List<Integer> managerIds;

    
    public FplLeague(Integer leagueId, List<Integer> managerIds) {
        this.leagueId = leagueId;
        this.managerIds = managerIds;
    }


    public Integer getLeagueId() {
        return leagueId;
    }
    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }
    public String getLeagueName() {
        return leagueName;
    }
    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }
    public List<Integer> getManagerIds() {
        return managerIds;
    }
    public void setManagerIds(List<Integer> managerIds) {
        this.managerIds = managerIds;
    }

    public void addManagerIds(List<Integer> managerIds) {
        for (Integer managerId : managerIds) {
            this.managerIds.add(managerId);
        }
    }


}
