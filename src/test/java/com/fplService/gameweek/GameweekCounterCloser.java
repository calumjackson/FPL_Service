package com.fplService.gameweek;

public class GameweekCounterCloser implements Runnable {
    
    final  GameweekCounter gameweekCounter;

    public GameweekCounterCloser(final GameweekCounter gameweekCounter) {
        this.gameweekCounter = gameweekCounter;
    }

    @Override
    public void run() {
        try {
            //  Thread.sleep(10000);
            gameweekCounter.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
