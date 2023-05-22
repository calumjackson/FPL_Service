package com.fplService.manager;


public final class ManagerGameweekGeneratorCloser implements Runnable {
    
    final  ManagerGameweekGenerator managerGameweekGeneratorConsumer;

    public ManagerGameweekGeneratorCloser(final ManagerGameweekGenerator managerGameweekGenerator) {
        this.managerGameweekGeneratorConsumer = managerGameweekGenerator;
    }

    @Override
    public void run() {
        try {
            managerGameweekGeneratorConsumer.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}