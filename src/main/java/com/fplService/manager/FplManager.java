package com.fplService.manager;

import com.google.gson.Gson;

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

    public FplManager(String managerJson) {
        FplManager manager = new Gson().fromJson((managerJson), FplManager.class);
        this.managerId = manager.managerId;
        this.managerFirstName = manager.managerFirstName;
        this.managerLastName = manager.managerLastName;
        this.teamName = manager.teamName;
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
        this.managerFirstName = managerName.replace("\"", "\\\"");
    }

    public void setManagerLastName(String managerName) {
        this.managerLastName = managerName.replace("\"", "\\\"");
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getTeamName() {
        return teamName.replace("\\", "\\\\").replace("\\\"", "\"");
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String toString() {

        String formatString = "{ \"managerFirstName\": \"%s\", "
                + "\"managerLastName\": \"%s\", "
                + "\"managerId\": \"%d\", "
                + "\"teamName\": \"%s\" }";

        return String.format(formatString, getManagerFirstName(), getManagerLastName(),
                        getManagerId(), getTeamName());
    }  

    public FplManager decodeManagerJson(String managerJson) {

        FplManager manager = new Gson().fromJson((managerJson), FplManager.class);        
        return manager;
        
    }

}
