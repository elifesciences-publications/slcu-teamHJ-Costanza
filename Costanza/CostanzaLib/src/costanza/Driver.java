package costanza;

public class Driver implements Runnable {
    /** Queue with jobs. */
    private Queue jobs;
    
    /** Current case. */
    private Case currentCase;
    
    /** Factory for Processor creation. */
    private Factory<Processor> factory;
    
    /**Constructor for the Driver class.
     * @param jobs the Queue of jobs to run.
     * @param currentCase the Case that the jobs should work on.
     * @param factory the Factory that we use to create each Processor.
     */
    public Driver(Queue jobs, Case currentCase, Factory<Processor> factory) {
        this.jobs = jobs;
        this.currentCase = currentCase;
        this.factory = factory;
    }
    
    /**Runs the jobs in the Queue. */
    //@Override
    public void run() {
		try {
			jobs.run(factory, currentCase);
		} catch (Exception exception) {
			System.err.println("Costanza Driver caught an exception: " + exception.getMessage());
			exception.printStackTrace();
		}
	}
}
