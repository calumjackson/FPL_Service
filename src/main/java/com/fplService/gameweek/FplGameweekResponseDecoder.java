package com.fplService.gameweek;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class FplGameweekResponseDecoder {

    public List<FplGameweek> decodeResponse(String responseBody) {

        ManagerGameweeks data = new Gson().fromJson((responseBody), ManagerGameweeks.class);

        List<FplGameweek> gameweeks = new ArrayList<FplGameweek>();

        for (GameweekData gameweek : data.getCurrent())
            gameweeks.add( new FplGameweek(
                null
                , gameweek.getEvent()
                , null
                , gameweek.getPoints()
                , gameweek.getPoints_on_bench()
                , gameweek.getEvent_transfers_cost())
            )
            ;

        return gameweeks;
    }    


    class ManagerGameweeks {
        public GameweekData[] getCurrent() {
            return current;
        }

        public void setCurrent(GameweekData[] current) {
            this.current = current;
        }

        GameweekData[] current;


        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for(GameweekData current1 : current) {
                sb.append(current1);
                if(!current1.equals(current[current.length-1])) {
                    sb.append(",");
                };
            }
            sb.append("}");
            return sb.toString();
        }


    }

    public class GameweekData {

        Integer event;
        Integer points;
        Integer total_points;
        Integer points_on_bench;
        Integer event_transfers_cost;

        public Integer getEvent() {
            return event;
        }



        public void setEvent(Integer event) {
            this.event = event;
        }



        public Integer getPoints() {
            return points;
        }



        public void setPoints(Integer points) {
            this.points = points;
        }



        public Integer getTotal_points() {
            return total_points;
        }



        public void setTotal_points(Integer total_points) {
            this.total_points = total_points;
        }



        public Integer getPoints_on_bench() {
            return points_on_bench;
        }



        public void setPoints_on_bench(Integer points_on_bench) {
            this.points_on_bench = points_on_bench;
        }



        public Integer getEvent_transfers_cost() {
            return event_transfers_cost;
        }



        public void setEvent_transfers_cost(Integer event_transfer_cost) {
            this.event_transfers_cost = event_transfer_cost;
        }



        public String toString() {
            return String.format(" \"event\" : \"%s\", \"points\" : \"%d\"", event, points, total_points);
        }
    }

}
