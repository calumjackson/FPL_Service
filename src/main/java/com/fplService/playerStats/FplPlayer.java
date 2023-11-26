package com.fplService.playerStats;

public class FplPlayer {
    
    private String first_name; //first_name
    private String second_name; //second_name
    private Integer id; //id
    private Integer element_type; // position
    private Integer team; //team
    private Float selected_by_percent;
    private Integer now_cost;

    private Integer transfers_in;
    private Integer transfers_in_event;
    private Integer transfers_out;
    private Integer transfers_out_event;
    private Integer penalties_order;
    private Integer cost_change_event;
    

    public Integer getCost_change_event() {
        return cost_change_event;
    }


    public void setCost_change_event(Integer cost_change_event) {
        this.cost_change_event = cost_change_event;
    }


    public Integer getPenalties_order() {
        if (penalties_order == null) {
            penalties_order = 0;
        }
        return penalties_order;
    }


    public void setPenalties_order(Integer penalties_order) {
        
        this.penalties_order = penalties_order;
    }


    public Integer getTransfers_in() {
        return transfers_in;
    }


    public void setTransfers_in(Integer transfers_in) {
        this.transfers_in = transfers_in;
    }


    public Integer getTransfers_in_event() {
        return transfers_in_event;
    }


    public void setTransfers_in_event(Integer transfers_in_event) {
        this.transfers_in_event = transfers_in_event;
    }


    public Integer getTransfers_out() {
        return transfers_out;
    }


    public void setTransfers_out(Integer transfers_out) {
        this.transfers_out = transfers_out;
    }


    public Integer getTransfers_out_event() {
        return transfers_out_event;
    }


    public void setTransfers_out_event(Integer transfers_out_event) {
        this.transfers_out_event = transfers_out_event;
    }


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
