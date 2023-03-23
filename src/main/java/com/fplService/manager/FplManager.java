package com.fplService.manager;

public class FplManager {

    private String managerFirstName;
    private String managerLastName;
    private Integer managerId;
    private String teamName;

 


    public FplManager(Integer managerId, String managerFirstName, String managerLastName, String teamName) {
        this.managerId = managerId;
        this.managerFirstName = managerFirstName;
        this.managerLastName = managerLastName;
        this.teamName = teamName;

    }
    
    public String getManagerFirstName() {
        return managerFirstName;
    }

    public String getManagerLastName() {
        return managerLastName;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerFirstName(String managerName) {
        this.managerFirstName = managerName;
    }

    public void setManagerLastName(String managerName) {
        this.managerLastName = managerName;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
