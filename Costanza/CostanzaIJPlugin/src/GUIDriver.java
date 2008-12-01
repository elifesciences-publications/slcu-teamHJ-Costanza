
import costanza.Case;
import costanza.Factory;
import costanza.Processor;

/**
 *
 * @author pawel
 */
public class GUIDriver implements Runnable {

    /** Queue with jobs. */
    private GUIQueue jobs;
    /** Current case. */
    private Case currentCase;
    /** Factory for Processor creation. */
    private Factory<Processor> factory;

    /**Constructor for the Driver class.
     * @param jobs the Queue of jobs to run.
     * @param currentCase the Case that the jobs should work on.
     * @param factory the Factory that we use to create each Processor.
     */
    public GUIDriver(GUIQueue jobs, Case currentCase, Factory<Processor> factory) {
        this.jobs = jobs;
        this.currentCase = currentCase;
        this.factory = factory;
    }

    /**Runs the jobs in the Queue. */
    //@Override
    public void run() {
        try {
            jobs.run(factory, currentCase);
        } catch (Exception e) {
            System.err.println("GUIDriver caught an exception: " + e.getMessage());
//            exception.printStackTrace();
        }
    }
}
