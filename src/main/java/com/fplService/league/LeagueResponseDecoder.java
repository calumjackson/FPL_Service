package com.fplService.league;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class LeagueResponseDecoder {

    List<Integer> managerIds = new ArrayList<Integer>();
    Boolean hasNext;

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<Integer> getManagerIds() {
        return managerIds;
    }

    public void setManagerIds(List<Integer> managerIds) {
        this.managerIds = managerIds;
    }

    public List<Integer> decodeResponse(String responseBody) {

        LeagueDataShell data = null;
        data = new Gson().fromJson((responseBody), LeagueDataShell.class);

        System.out.println("Has next :" + data.getStandings().getHas_next());
        addManagerIDs(data.getStandings().getManagerIds());
        hasNext = data.getStandings().getHasNext();
        return managerIds;

    }   

    public void addManagerIDs(List<Integer> responseManagerIds) {
        for (Integer managerId : responseManagerIds) {
            this.managerIds.add(managerId);
        }
        
    }
    
    public boolean decodeResponses(String responseBody) {

        LeagueDataShell data = null;
        data = new Gson().fromJson((responseBody), LeagueDataShell.class);

        // System.out.println("Has next :" + data.getStandings().getHas_next());
        addManagerIDs(data.getStandings().getManagerIds());
        hasNext = data.getStandings().getHasNext();
        return getHasNext();
    }


    class LeagueDataShell {
        // Standings is a object {} and will need to be dealt differently to []
        public ResultsDataShell getStandings() {
            return standings;
        }

        public void setCurrent(ResultsDataShell standings) {
            this.standings = standings;
        }

        ResultsDataShell standings;

        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append("{");

            // for (ResultsDataShell standing : standings) {
                
            System.out.println("This is the page: "+ standings.getPage());    
            
            sb.append("}");
            return sb.toString();
        }
        

    }

    class ResultsDataShell {
        
        String page;
        TeamList[] results;
        String has_next;

        public String getHas_next() {
            return has_next;
        }

        public void setHas_next(String has_next) {
            this.has_next = has_next;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }


        public TeamList[] getResults() {
            return results;
        }

        public void setResults(TeamList[] results) {
            this.results = results;
        }

        public List<Integer> getManagerIds() {
            
            List<Integer> managerIds = new ArrayList<Integer>();
            for(TeamList team : results) {
                managerIds.add(Integer.parseInt(team.entry));
            }
            
            return managerIds;
        }

        public Boolean getHasNext() {
            
            if (has_next.equals("true")) {
                return true;
            } 

            return false;
        }
        

        public String toString() {

            StringBuilder sb = new StringBuilder();
            sb.append("{");
                
            for(TeamList result : results) {
                    System.out.println(result.getPlayer_name());
                    sb.append(result.getPlayer_name());
                if(!result.equals(results[results.length-1])) {
                    sb.append(",");
                };
            }

            sb.append("}");
            return sb.toString();
        }
    }

    public class TeamList {

        String entry;
        String player_name;
        

        public String getEntry() {
            return entry;
        }


        public void setEntry(String entry) {
            this.entry = entry;
        }


        public String getPlayer_name() {
            return player_name;
        }


        public void setPlayer_name(String player_name) {
            this.player_name = player_name;
        }


        public String toString() {
            return String.format(" \"id\" : \"%s\", \"player_name\" : \"%d\"", entry, player_name);
        }
    }

    
}
