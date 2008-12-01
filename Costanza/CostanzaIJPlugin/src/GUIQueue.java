
import costanza.Job;
import costanza.Case;
import costanza.Factory;
import costanza.Processor;
import costanza.Queue;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class wraps costanza Queue to display interim results of processing in the text box.
 * @author pawel
 */
public class GUIQueue {

    /** Queue containing the job queue. */
    private Queue queue;
    private java.awt.TextField textField;
    private Costanza_Plugin plugin;
//    private PropertyChangeSupport propertyChange;
//    private int progress;
//    private int totalJobs;
    /** Constructor for GUIQueue links to costanza Queue and gets text field to display the results to*/
    public GUIQueue(Queue q, java.awt.TextField tf, Costanza_Plugin p) {
        queue = q;
        textField = tf;
        plugin = p;
//        propertyChange = new PropertyChangeSupport(this);
//        totalJobs = queue.size();
    }

//    public void addPropertyChangeListener(PropertyChangeListener listener){
//        propertyChange.addPropertyChangeListener("progress", listener);
//    }
    /**Process queue.
     * This member function steps through the queue and calls process() in all processors.
     * @param factory the Factory that creates each Processor.
     * @param currentCase the Case that each Processor should work on.
     * @throws java.lang.Exception
     */
    private class RunSetEnabledOptions implements Runnable {

        private boolean choice;

        RunSetEnabledOptions(boolean b) {
            choice = b;
        }

        public void run() {
            plugin.getFrame().setEnabledOptions(choice);
            plugin.getFrame().setMenuAndButtonsEnabled(choice);
        }
    }

    public void run(Factory<Processor> factory, Case currentCase) throws Exception {
        int counter = 0;
        int total = queue.size();
        EventQueue.invokeLater(new RunSetEnabledOptions(false));
        while (true) {
            Job job = queue.poll();
            if (job == null) {
                break;
            }
            Processor processor;

            processor = factory.create(job.getProcessorId());
//            System.out.println(Thread.currentThread().getName());
            System.out.println("Running: " + job.getProcessorId());
            if(Thread.interrupted()){
                throw new InterruptedException("Aborted by user");
            }
            String message = "task " + String.valueOf(++counter) + " out of " + String.valueOf(total) + " (" + job.getProcessorId() + ")";
            EventQueue.invokeLater(new RunDisplayText(message));
            processor.process(currentCase, job.getOptions());
        }
        EventQueue.invokeLater(new RunDisplayText("Finished processing tasks."));
        EventQueue.invokeLater(new RunProcessResults(plugin)); 

    }

    private class RunDisplayText implements Runnable {

        private String message;

        public RunDisplayText(String s) {
            message = s;
        }

        public void run() {
            textField.setText(message);
        }
    }

    private class RunProcessResults implements Runnable {

        private Costanza_Plugin plugin;

        public RunProcessResults(Costanza_Plugin p) {
            plugin = p;
        }

        public void run() {
            if (plugin.getSecondatrStackOption() == true) {
                plugin.getFrame().askForSecondaryStack();
            } else {
                try {
                    plugin.showFinalResults();
                } catch (Exception ex) {
                    Logger.getLogger(GUIQueue.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
