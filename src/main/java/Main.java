import com.fplService.gameweek.FplGameweekFactory;
import com.fplService.league.FplLeague;
import com.fplService.league.FplLeagueFactory;
import com.fplService.manager.FplManagerFactory;
import com.fplService.managerDatabase.FplManagerDBFactory;
import com.fplService.team_producer.team_producer;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {

            clearDownManagers();
            Integer leagueId = 57365;

            // Get the league details
            FplLeague fplLeague = new FplLeagueFactory().createFplLeage(leagueId);
            List<Integer> managerIds = fplLeague.getManagerIds();

            // Create all the managers in the league
             for (Integer managerId : managerIds) {
                new FplManagerFactory().createFplManager(managerId);
             }

            // Get the gameweek totals for each manager.
            for (Integer managerId : managerIds) {
                populateGameweekTotals(managerId);
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            
        }
    }

    private static void populateGameweekTotals(Integer managerId) {
        new FplGameweekFactory().createManagerGameweek(managerId);
    }

    private static void clearDownManagers() throws SQLException {
        new FplManagerDBFactory().deleteAllManagers();
    }

}