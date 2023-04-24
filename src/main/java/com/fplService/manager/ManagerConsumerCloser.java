package com.fplService.manager;


public final class ManagerConsumerCloser implements Runnable {
    
    final  ManagerConsumer managerConsumer;

    public ManagerConsumerCloser(final ManagerConsumer managerConsumer) {
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