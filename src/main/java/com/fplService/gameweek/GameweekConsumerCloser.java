package com.fplService.gameweek;

public final class GameweekConsumerCloser implements Runnable {
    
    final  GameweekConsumer managerConsumer;

    public GameweekConsumerCloser(final GameweekConsumer managerConsumer) {
        this.managerConsumer = managerConsumer;
    }

    @Override
    public void run() {
        try {
            managerConsumer.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}