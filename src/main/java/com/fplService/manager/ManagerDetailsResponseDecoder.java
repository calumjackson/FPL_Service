package com.fplService.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;


public class ManagerDetailsResponseDecoder {

    Logger logger;
    
    public FplManager decodeResponse(String responseBody) {
        logger = LoggerFactory.getLogger(ManagerDetailsResponseDecoder.class);
        ManagerDetails managerDetails = null;
        managerDetails = new Gson().fromJson((responseBody), ManagerDetails.class);

        return new FplManager(managerDetails.getManagerId(), managerDetails.getPlayer_first_name(), managerDetails.getPlayer_last_name(), managerDetails.getName());


    }

    class ManagerDetails {

        String id;
        String player_first_name;        
        String player_last_name;
        String name;
        
        public Integer getManagerId() {
            return Integer.parseInt(id);
        }

        public void setManagerId(String id) {
            this.id = id;
        }


        public String getPlayer_last_name() {
            return player_last_name.replace("\"", "\\\"");
        }

        public void setPlayer_last_name(String player_last_name) {
            this.player_last_name = player_last_name.replace("\"", "\\\"");
        }

        public String getName() {
            return name.replace("\"", "\\\"");
        }

        public void getName(String name) {
            this.name = name.replace("\"", "\\\"");
        }

        public String getPlayer_first_name() {

            return player_first_name.replace("\"", "\\\"");
        }

        public void setPlayer_first_name(String player_first_name) {
           this.player_first_name = player_first_name.replace("\"", "\\\"");
        }


        public String toString() {
            return String.format(" \"id\" : \"%s\", \"firstname\" : \"%t\", \"lastname\" : \"%x\"", id, player_first_name, player_last_name);
        }
    }

    
}
