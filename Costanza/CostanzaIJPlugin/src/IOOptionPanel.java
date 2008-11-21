
//public class IOOptionPanel extends java.awt.Panel {
public class IOOptionPanel extends java.awt.Panel {

    private MainFrame frame;

    public IOOptionPanel(MainFrame mainFrame) {
        frame = mainFrame;
        initComponents();
        setFont(frame.getFrameFont());
    }

    public int getResultRequest() {
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
        if (workingStackCheckbox.getState() == true) {
            request = request | Costanza_Plugin.REQUEST_WORKING_STACK;
        }
        return request;
    }
    
    public void setBOARequest(Boolean b) {
        boaColourizeCheckbox.setState(b);
    }
    
    public void setBOAIntensityRequest(Boolean b) {
        boaIntensityColourizeCheckbox.setState(b);
    }
    
    public void setCellCenterRequest(Boolean b) {
        cellCenterMarkerOption.setState(b);
        setMarkerSizeEnable();
    }
    
    public void setWorkingStackRequest(Boolean b) {
        workingStackCheckbox.setState(b);
    }
    
    public boolean getSecondaryStackOption() {
        return secondaryStackCheckbox.getState();
    }

    public boolean getExtendedNeighborhoodOption() {
        return extendedNeighborhoodOption.getState();
    }

    public boolean getPlateauOption() {
        return plateauOption.getState();
    }

    public Integer getMarkerRadius() {
        return new Integer(markerRadiusTextField.getText());
    }
    
    public void setSecondaryStackOption(Boolean b) {
        secondaryStackCheckbox.setState(b);
    }

    public void setExtendedNeighborhoodOption(Boolean b) {
        extendedNeighborhoodOption.setState(b);
    }

    public void setPlateauOption(Boolean b) {
        plateauOption.setState(b);
    }

    public void setMarkerRadius(Integer i) {
        markerRadiusTextField.setText(i.toString());
    }
    
    private void setMarkerSizeEnable(){
        if(cellCenterMarkerOption.getState() == true)
            markerRadiusTextField.setEnabled(true);
        else
            markerRadiusTextField.setEnabled(false);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainOptions = new java.awt.Label();
        extendedNeighborhoodOption = new java.awt.Checkbox();
        plateauOption = new java.awt.Checkbox();
        inputOptionsLabel = new java.awt.Label();
        secondaryStackCheckbox = new java.awt.Checkbox();
        outputOptionsLabel = new java.awt.Label();
        cellCenterMarkerOption = new java.awt.Checkbox();
        markerRadiusTextField = new java.awt.TextField();
        boaColourizeCheckbox = new java.awt.Checkbox();
        boaIntensityColourizeCheckbox = new java.awt.Checkbox();
        workingStackCheckbox = new java.awt.Checkbox();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new java.awt.GridBagLayout());

        mainOptions.setFont(new java.awt.Font("Dialog", 1, 12));
        mainOptions.setText("Gradient descent options:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(mainOptions, gridBagConstraints);

        extendedNeighborhoodOption.setLabel("Use extended (box) neighborhood");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(extendedNeighborhoodOption, gridBagConstraints);

        plateauOption.setLabel("Mark intensity plateau with single maximum");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(plateauOption, gridBagConstraints);

        inputOptionsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        inputOptionsLabel.setText("Input options:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(inputOptionsLabel, gridBagConstraints);

        secondaryStackCheckbox.setLabel("Use secondary stack to measure intensity levels.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(secondaryStackCheckbox, gridBagConstraints);

        outputOptionsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        outputOptionsLabel.setText("Output options:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(outputOptionsLabel, gridBagConstraints);

        cellCenterMarkerOption.setLabel("Mark cell centers. Marker pixel radius:");
        cellCenterMarkerOption.setState(true);
        cellCenterMarkerOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cellCenterMarkerOptionItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(cellCenterMarkerOption, gridBagConstraints);

        markerRadiusTextField.setColumns(1);
        markerRadiusTextField.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(markerRadiusTextField, gridBagConstraints);

        boaColourizeCheckbox.setLabel("Display basins of attractions (BOA).");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(boaColourizeCheckbox, gridBagConstraints);

        boaIntensityColourizeCheckbox.setLabel("Display basins of attractions according to measured intensity.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(boaIntensityColourizeCheckbox, gridBagConstraints);

        workingStackCheckbox.setLabel("Display internal working stack.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(workingStackCheckbox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cellCenterMarkerOptionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cellCenterMarkerOptionItemStateChanged
        setMarkerSizeEnable();
    }//GEN-LAST:event_cellCenterMarkerOptionItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Checkbox boaColourizeCheckbox;
    private java.awt.Checkbox boaIntensityColourizeCheckbox;
    private java.awt.Checkbox cellCenterMarkerOption;
    private java.awt.Checkbox extendedNeighborhoodOption;
    private java.awt.Label inputOptionsLabel;
    private java.awt.Label mainOptions;
    private java.awt.TextField markerRadiusTextField;
    private java.awt.Label outputOptionsLabel;
    private java.awt.Checkbox plateauOption;
    private java.awt.Checkbox secondaryStackCheckbox;
    private java.awt.Checkbox workingStackCheckbox;
    // End of variables declaration//GEN-END:variables
}
