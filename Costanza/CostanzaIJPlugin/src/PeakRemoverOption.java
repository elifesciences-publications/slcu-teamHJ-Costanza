
import costanza.Job;
import costanza.Options;
import costanza.Queue;

public class PeakRemoverOption extends java.awt.Panel implements ProcessorOption {

	public PeakRemoverOption() {
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

        sizeThresholdLabel = new java.awt.Label();
        sizeThresholdTextField = new java.awt.TextField();
        intensityThresholdLabel = new java.awt.Label();
        intensityThresholdTextField = new java.awt.TextField();

        setLayout(new java.awt.GridBagLayout());

        sizeThresholdLabel.setText("Size threshold:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(sizeThresholdLabel, gridBagConstraints);

        sizeThresholdTextField.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(sizeThresholdTextField, gridBagConstraints);

        intensityThresholdLabel.setText("Intensity threshold:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(intensityThresholdLabel, gridBagConstraints);

        intensityThresholdTextField.setText("0.2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(intensityThresholdTextField, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
	public java.awt.Panel getPanel() {
		return this;
	}

	public String getProcessorName() {
		return "Peak remover";
	}

	public void addJobs(Queue jobs) throws Exception {
		Options options = new Options();
		options.addOption("sizeThreshold", new Float(sizeThresholdTextField.getText()));
		options.addOption("intensityThreshold", new Float(intensityThresholdTextField.getText()));
		jobs.addJob(new Job("peakremover", options));
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Label intensityThresholdLabel;
    private java.awt.TextField intensityThresholdTextField;
    private java.awt.Label sizeThresholdLabel;
    private java.awt.TextField sizeThresholdTextField;
    // End of variables declaration//GEN-END:variables
}