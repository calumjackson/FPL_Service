import com.fplService.databaseConnection.FplDatabaseConnector;
import com.fplService.gameweek.FplGameweekFactory;
import com.fplService.gameweek.GameweekConsumer;
import com.fplService.gameweek.GameweekConsumerCloser;
import com.fplService.gameweek.GameweekProducer;
import com.fplService.league.FplLeague;
import com.fplService.league.FplLeagueFactory;
import com.fplService.manager.FplManagerFactory;
import com.fplService.manager.ManagerConsumer;
import com.fplService.manager.ManagerConsumerCloser;
import com.fplService.manager.ManagerProducer;
import com.fplService.managerDatabase.FplManagerDBFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.debug("Log mesage 1");

        ManagerConsumer managerConsumer = null;
        GameweekConsumer gameweekConsumer = null;
        new GameweekProducer();
        new ManagerProducer();
        CountDownLatch latch = new CountDownLatch(2);

        try {
            
            clearDownData();
            Integer leagueId = 57365;
            
            // Get the league details
            FplLeague fplLeague = new FplLeagueFactory().createFplLeage(leagueId);
            List<Integer> managerIds = fplLeague.getManagerIds();
            
            managerConsumer = new ManagerConsumer(latch);
            new Thread(managerConsumer).start();
            Runtime.getRuntime().addShutdownHook(new Thread(new ManagerConsumerCloser(managerConsumer)));
            
            gameweekConsumer = new GameweekConsumer(latch);
            new Thread(gameweekConsumer).start();
            Runtime.getRuntime().addShutdownHook(new Thread(new GameweekConsumerCloser(gameweekConsumer)));

            // Create all the managers in the league
             for (Integer managerId : managerIds) {
                new FplManagerFactory().createFplManager(managerId);
             }
            
            // Get the gameweek totals for each manager.
            for (Integer managerId : managerIds) {
                populateGameweekTotals(managerId);
             }

            // Wait a little bit for the messages to be processed on the separate threads.
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            closeDatabase();
            closeProducers();  
            System.exit(0);          
        }
    }

    private static void closeProducers() {
        GameweekProducer.closeProducer();
        ManagerProducer.closeProducer();
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