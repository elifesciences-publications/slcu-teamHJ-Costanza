package Costanza;

import java.lang.Exception;
import java.util.Vector;

public class Queue {
    /** Queue containing the job queue. */
    private java.util.Queue<Job> jobs;
    
    /** Processor factory. */
    private Factory factory;
    
    /** Current Case. */
    private Case currentCase;
    
    /** Constructor for Queue class. */
    public Queue(Factory factory, Case currentCase) {
        this.factory = factory;
        this.currentCase = currentCase;
    }
    
    /** Add job to queue. */
    public void addJob(Job job) throws Exception {
        if (!jobs.offer(job)) {
            throw new Exception("Job queue unable to insert new job.");
        }
    }
    
    /** Process queue.
     *
     * This member function steps through the queue and calls process() in all processors.
     */
    public void run() throws Exception {
        while (true) {
            Job job = jobs.poll();
            if (job == null) {
                return;
            }
            Processor processor;
            try {
                processor = factory.createProcessor(job.getProcessorId());
            } catch (Exception exception) {
                throw exception;
            }
            processor.process(currentCase, job.getOptions());
        }
    }
}