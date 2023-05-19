package com.fplService.managerDatabase;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

import com.fplService.manager.FplManagerDBFactory;

public class FPLManagerDBFactoryTest {
    
    @Test
    public void testDeleteAllManagers() {

        Integer managerCountValue = -1;
        FplManagerDBFactory managerFactory = new FplManagerDBFactory();
        try {
            managerFactory.deleteAllManagers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            managerCountValue = managerFactory.getManagerCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Integer expected = 0;
        assertEquals(expected, managerCountValue);

    } 

    @Test
    public void testDeleteAllGameweeks() {

        Integer gameweekCountValue = -1;
        FplManagerDBFactory managerFactory = new FplManagerDBFactory();
        try {
            managerFactory.deleteAllGameweeks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            gameweekCountValue = managerFactory.getGameweekCount();
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Integer expected = 0;
        assertEquals(expected, gameweekCountValue);

    } 



}
