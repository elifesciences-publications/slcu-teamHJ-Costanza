
import costanza.Job;
import costanza.Options;
import costanza.Queue;
import java.awt.Panel;

public class InvertOption extends java.awt.Panel implements ProcessorOption {

    public InvertOption() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        invertCheckbox = new java.awt.Checkbox();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new java.awt.GridBagLayout());

        invertCheckbox.setLabel("Invert image.");
        invertCheckbox.setVisible(false);
        add(invertCheckbox, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents
    public Panel getPanel() {
        return this;
    }

    public String getProcessorName() {
        return new String("Invert image");
    }

    public void addJobs(Queue jobs) throws Exception {
        jobs.addJob(new Job("invert", getOptions()));
    }

    public Options getOptions() throws Exception {
        return null;
    }
    
    public void setFromOptions(Options o) throws Exception {
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Checkbox invertCheckbox;
    // End of variables declaration//GEN-END:variables
}
