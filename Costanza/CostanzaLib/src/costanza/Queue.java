package costanza;

import java.lang.Exception;
import java.util.Vector;

public class Queue {
    /** Queue containing the job queue. */
    private java.util.LinkedList<Job> jobs;
    
    /** Constructor for Queue. */
    public Queue() {
        jobs = new java.util.LinkedList<Job>();
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
    public void run(Factory<Processor> factory, Case currentCase) throws Exception {
        while (true) {
            Job job = jobs.poll();
            if (job == null) {
                return;
            }
            Processor processor;
            try {
                processor = factory.create(job.getProcessorId());
            } catch (Exception exception) {
                throw exception;
            }
            processor.process(currentCase, job.getOptions());
        }
    }
}