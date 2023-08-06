package com.fplService.bootstrap;

public class FPLTeams {
    
    private String name; //first_name
    private Integer id; //id

   
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FPLTeams(Integer id, String playerName) {
        this.name = playerName;
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String playerFirstName) {
        this.name = playerFirstName;
    }

}
