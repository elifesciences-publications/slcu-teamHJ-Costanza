
public class IOOptionPanel extends java.awt.Panel {

	private MainFrame frame;

	public IOOptionPanel(MainFrame mainFrame) {
		frame = mainFrame;
		initComponents();
	}

	int getResultRequest() {
		int request = 0;
		if (boaColourizeCheckbox.getState() == true) {
			request = request | Costanza_Plugin.REQUEST_BOA_COLORIZER;
		}
		if (boaIntensityColourizeCheckbox.getState() == true) {
			request = request | Costanza_Plugin.REQUEST_BOA_INTENSITY_COLORIZER;
		}
		if (cellCenterMarkerOption.getState() == true) {
			request = request | Costanza_Plugin.REQUEST_CELL_MARKER;
		}
		return request;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        boaColourizeCheckbox = new java.awt.Checkbox();
        outputOptionsLabel = new java.awt.Label();
        cellCenterMarkerOption = new java.awt.Checkbox();
        boaIntensityColourizeCheckbox = new java.awt.Checkbox();

        setLayout(new java.awt.GridBagLayout());

        boaColourizeCheckbox.setLabel("Display basins of attractions (BOA).");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(boaColourizeCheckbox, gridBagConstraints);

        outputOptionsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        outputOptionsLabel.setText("Output options");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(outputOptionsLabel, gridBagConstraints);

        cellCenterMarkerOption.setLabel("Mark cell centers.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(cellCenterMarkerOption, gridBagConstraints);

        boaIntensityColourizeCheckbox.setLabel("Display basins of attractions according to measured intensity.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(boaIntensityColourizeCheckbox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Checkbox boaColourizeCheckbox;
    private java.awt.Checkbox boaIntensityColourizeCheckbox;
    private java.awt.Checkbox cellCenterMarkerOption;
    private java.awt.Label outputOptionsLabel;
    // End of variables declaration//GEN-END:variables
}