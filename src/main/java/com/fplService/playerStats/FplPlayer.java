package com.fplService.playerStats;

public class FplPlayer {
    
    private String first_name; //first_name
    private String second_name; //second_name
    private Integer id; //id
    private Integer element_type; // position
    private Integer team; //team
    private Float selected_by_percent;
    private Integer now_cost;


    public String position_converter() {
        Integer position = this.getElement_type();
        String pos_string = "Unknown";
        if (position == 1) {
            pos_string = "Goalkeeper";
        } else if (position == 2) {
            pos_string = "Defender";
        } else if (position == 3) {
            pos_string = "Midfielder";
        } else if (position == 4) {
            pos_string = "Forward";
        }
        return pos_string;
    }


    public Integer getNow_cost() {
        return now_cost;
    }

    public void setNow_cost(Integer now_cost) {
        this.now_cost = now_cost;
    }

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

    public Float getSelected_by_percent() {
        return selected_by_percent;
    }

    public void setSelected_by_percent(Float selected_by_percent) {
        this.selected_by_percent = selected_by_percent;
    }

    public String toString() {
            return String.format(" \"id\" : \"%s\", \"firstname\" : \"%t\", \"lastname\" : \"%x\"", id, first_name, second_name);
        }


}
