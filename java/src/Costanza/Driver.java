package Costanza;

import java.lang.Exception;

public class Driver implements Runnable {
    /** Queue with jobs. */
    private Queue jobs;
    
    /** Current case. */
    private Case currentCase;
    
    /** Factory for Processor creation. */
    private Factory factory;
    
    public Driver(Queue jobs, Case currentCase, Factory factory) {
        this.jobs = jobs;
        this.currentCase = currentCase;
        this.factory = factory;
    }
    
    public void run() {
        try {
            jobs.run(factory, currentCase);
        } catch (Exception exception) {
            java.lang.System.err.println(exception.getMessage());
        }
    }
}