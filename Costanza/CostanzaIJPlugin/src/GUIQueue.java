
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

    /** Constructor for GUIQueue links to costanza Queue and gets text field to display the results to*/
    public GUIQueue(Queue q, java.awt.TextField tf, Costanza_Plugin p) {
        queue = q;
        textField = tf;
        plugin = p;
    }


    /**Process queue.
     * This member function steps through the queue and calls process() in all processors.
     * @param factory the Factory that creates each Processor.
     * @param currentCase the Case that each Processor should work on.
     * @throws java.lang.Exception
     */

    public void run(Factory<Processor> factory, Case currentCase) throws Exception {
        int counter = 0;
        int total = plugin.countTotalTasks();
        Float step = 100.0f / (total+0.3f);
        EventQueue.invokeLater(new RunSetIndeterminate(false));
        EventQueue.invokeLater(new RunIncreaseProgress(0.3f*step));
        while (true) {
            if (Thread.interrupted()) {
                throw new InterruptedException("Aborted by user");
            }
            Job job = queue.poll();
            if (job == null) {
                break;
            }
            Processor processor = factory.create(job.getProcessorId());

            System.out.println("Running: " + job.getProcessorId());
            String message = "task " + String.valueOf(++counter) + " out of " + String.valueOf(total) + " (" + job.getProcessorId() + ")";
            EventQueue.invokeLater(new RunDisplayText(message));
            processor.process(currentCase, job.getOptions());
            EventQueue.invokeLater(new RunIncreaseProgress(step));
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

    private class RunIncreaseProgress implements Runnable {

        private int i;

        public RunIncreaseProgress(Float f) {
            this.i = f.intValue();
        }

        public void run() {
            plugin.increaseProgress(i);
        }
    }

    private class RunSetIndeterminate implements Runnable {

        private boolean b;

        public RunSetIndeterminate(boolean b) {
            this.b = b;
        }

        public void run() {
            plugin.setIndeterminate(b);
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
