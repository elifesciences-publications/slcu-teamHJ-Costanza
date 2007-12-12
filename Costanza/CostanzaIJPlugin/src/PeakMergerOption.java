
import costanza.Job;
import costanza.Options;
import costanza.Queue;

public class PeakMergerOption extends java.awt.Panel implements ProcessorOption {

	public PeakMergerOption() {
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        radiusLabel = new java.awt.Label();
        radiusTextField = new java.awt.TextField();

        setLayout(new java.awt.GridBagLayout());

        radiusLabel.setText("Radius:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(radiusLabel, gridBagConstraints);

        radiusTextField.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(radiusTextField, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
	public java.awt.Panel getPanel() {
		return this;
	}

	public String getProcessorName() {
		return "Peak merger";
	}

	public void addJobs(Queue jobs) throws Exception {
		Options options = new Options();
		options.addOption("radius", new Float(radiusTextField.getText()));
		jobs.addJob(new Job("peakmerger", options));
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Label radiusLabel;
    private java.awt.TextField radiusTextField;
    // End of variables declaration//GEN-END:variables
}