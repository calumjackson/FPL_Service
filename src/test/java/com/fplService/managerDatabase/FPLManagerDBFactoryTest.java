package com.fplService.managerDatabase;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

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
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Integer expected = 0;
        assertEquals(expected, managerCountValue);

    } 

}
