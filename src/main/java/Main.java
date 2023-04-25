import com.fplService.databaseConnection.FplDatabaseConnector;
import com.fplService.gameweek.FplGameweekFactory;
import com.fplService.league.FplLeague;
import com.fplService.league.FplLeagueFactory;
import com.fplService.manager.FplManagerFactory;
import com.fplService.manager.ManagerConsumer;
import com.fplService.manager.ManagerConsumerCloser;
import com.fplService.managerDatabase.FplManagerDBFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) {

        ManagerConsumer consumer = null;

        try {
            
            clearDownData();
            Integer leagueId = 57365;
            
            // Get the league details
            FplLeague fplLeague = new FplLeagueFactory().createFplLeage(leagueId);
            List<Integer> managerIds = fplLeague.getManagerIds();

            CountDownLatch latch = new CountDownLatch(1); 
            consumer = new ManagerConsumer(latch);
            new Thread(consumer).start();


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
            closeDatabase();
            Runtime.getRuntime().addShutdownHook(new Thread(new ManagerConsumerCloser(consumer)));
        }

        System.exit(0);
    }

    private static void closeDatabase() {
        try {
            FplDatabaseConnector.closeDatabaseConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void populateGameweekTotals(Integer managerId) {
        new FplGameweekFactory().createManagerGameweek(managerId);
    }

    private static void clearDownData() throws SQLException {
        new FplManagerDBFactory().deleteAllManagers();
        new FplManagerDBFactory().deleteAllGameweeks();
        
    }


}