package costanza;

public class Queue {
    /** Queue containing the job queue. */
    private java.util.LinkedList<Job> jobs;
    
    /** Constructor for Queue. */
    public Queue() {
        jobs = new java.util.LinkedList<Job>();
    }
    
    /** Add job to queue.
     * @param job the Job to add to the Queue.
     * @throws java.lang.Exception
     */
    public void addJob(Job job) throws Exception {
        if (!jobs.offer(job)) {
            throw new Exception("Job queue unable to insert new job.");
        }
    }
    
    /**
     * Polls the Job from the Queue.
     * @return job.
     */
    public Job poll(){
        return jobs.poll();
    }
    
    /**
     * Gets the size of the queue.
     * @return size.
     */
    public int size(){
        return jobs.size();
    }
    /**Process queue.
     * This member function steps through the queue and calls process() in all processors.
     * @param factory the Factory that creates each Processor.
     * @param currentCase the Case that each Processor should work on.
     * @throws java.lang.Exception
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
	    System.out.println("Running: " + job.getProcessorId());
            processor.process(currentCase, job.getOptions());         
        }
    }
}