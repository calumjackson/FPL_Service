package com.fplService.playerStats;

public class FplPlayer {
    
    private String first_name; //first_name
    private String second_name; //second_name
    private Integer id; //id
    private Integer element_type; // position
    private Integer team; //team
    
    public FplPlayer(String playerFirstName, String playerLastName, Integer playerId, Integer playerPosition,
            Integer playerTeam) {
        this.first_name = playerFirstName;
        this.second_name = playerLastName;
        this.id = playerId;
        this.element_type = playerPosition;
        this.team = playerTeam;
    }
    
    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String playerFirstName) {
        this.first_name = playerFirstName;
    }
    public String getSecond_name() {
        return second_name;
    }
    public void setSecond_name(String playerLastName) {
        this.second_name = playerLastName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer playerId) {
        this.id = playerId;
    }
    public Integer getElement_type() {
        return element_type;
    }
    public void setElement_type(Integer playerPosition) {
        this.element_type = playerPosition;
    }
    public Integer getTeam() {
        return team;
    }
    public void setTeam(Integer playerTeam) {
        this.team = playerTeam;
    }

    public String toString() {
            return String.format(" \"id\" : \"%s\", \"firstname\" : \"%t\", \"lastname\" : \"%x\"", id, first_name, second_name);
        }


}
