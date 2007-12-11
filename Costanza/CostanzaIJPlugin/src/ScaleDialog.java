
import ij.measure.Calibration;

public class ScaleDialog extends java.awt.Dialog {
	private Costanza_Plugin plugin;
	private Calibration calibration;
	
	public ScaleDialog(Costanza_Plugin plugin, boolean modal, ij.measure.Calibration calibration) {
		super(plugin.getMainFrame(), "Costanza Plugin", modal);
		this.plugin = plugin;
		this.calibration = calibration;
		
		initComponents();
		xScaleTextField.setText(Double.toString(calibration.pixelWidth));
		yScaleTextField.setText(Double.toString(calibration.pixelHeight));
		zScaleTextField.setText(Double.toString(calibration.pixelDepth));
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scaleLabel = new java.awt.Label();
        scalePanel = new java.awt.Panel();
        xScaleLabel = new java.awt.Label();
        xScaleTextField = new java.awt.TextField();
        yScaleLabel = new java.awt.Label();
        yScaleTextField = new java.awt.TextField();
        zScaleLabel = new java.awt.Label();
        zScaleTextField = new java.awt.TextField();
        buttonPanel = new java.awt.Panel();
        cancelButton = new java.awt.Button();
        continueButton = new java.awt.Button();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        scaleLabel.setText("Please enter scale of image.");
        add(scaleLabel);

        scalePanel.setLayout(new java.awt.GridBagLayout());

        xScaleLabel.setText("X-scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        scalePanel.add(xScaleLabel, gridBagConstraints);

        xScaleTextField.setText("1.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        scalePanel.add(xScaleTextField, gridBagConstraints);

        yScaleLabel.setText("Y-scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        scalePanel.add(yScaleLabel, gridBagConstraints);

        yScaleTextField.setText("1.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        scalePanel.add(yScaleTextField, gridBagConstraints);

        zScaleLabel.setText("Z-scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        scalePanel.add(zScaleLabel, gridBagConstraints);

        zScaleTextField.setText("1.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        scalePanel.add(zScaleTextField, gridBagConstraints);

        add(scalePanel);

        cancelButton.setLabel("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        continueButton.setLabel("Continue");
        continueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continueButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(continueButton);

        add(buttonPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents
	
	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		plugin.scaleDialogCancel(this);
	}//GEN-LAST:event_cancelButtonActionPerformed

	private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continueButtonActionPerformed
		calibration.pixelWidth = new Float(xScaleTextField.getText()).floatValue();
		calibration.pixelHeight = new Float(yScaleTextField.getText()).floatValue();
		calibration.pixelDepth= new Float(zScaleTextField.getText()).floatValue();
		plugin.scaleDialogContinue(this);
	}//GEN-LAST:event_continueButtonActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Panel buttonPanel;
    private java.awt.Button cancelButton;
    private java.awt.Button continueButton;
    private java.awt.Label scaleLabel;
    private java.awt.Panel scalePanel;
    private java.awt.Label xScaleLabel;
    private java.awt.TextField xScaleTextField;
    private java.awt.Label yScaleLabel;
    private java.awt.TextField yScaleTextField;
    private java.awt.Label zScaleLabel;
    private java.awt.TextField zScaleTextField;
    // End of variables declaration//GEN-END:variables
	
}
