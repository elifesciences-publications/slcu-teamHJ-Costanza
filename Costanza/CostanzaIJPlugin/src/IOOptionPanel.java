
//public class IOOptionPanel extends java.awt.Panel {
public class IOOptionPanel extends java.awt.Panel {

    private MainFrame frame;

    public IOOptionPanel(MainFrame mainFrame) {
        frame = mainFrame;
        initComponents();
        setFont(frame.getFrameFont());
    }

    public void setEnabledProcessingOptions(boolean b) {
        extendedNeighborhoodOption.setEnabled(b);
        plateauOption.setEnabled(b);
        secondaryStackCheckbox.setEnabled(b);
    }

    public void setEnabledDisplayOptions(boolean b) {
        boaColourizeCheckbox.setEnabled(b);
        boaIntensityColourizeCheckbox.setEnabled(b);
        cellCenterMarkerOption.setEnabled(b);
        workingStackCheckbox.setEnabled(b);
        markerRadiusTextField.setEnabled(b);
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
        setBOAIntensityNormalizeEnable();
    }

    public void setBOAIntensityNormalize(Boolean b) {
        boaIntensityNormalizeCheckbox.setState(b);
    }

    public void setIntensityChannels(Boolean r, Boolean g, Boolean b) {
        redChannelOption.setState(r);
        greenChannelOption.setState(g);
        blueChannelOption.setState(b);
    }
    
    public void setSecondaryIntensityChannels(Boolean r, Boolean g, Boolean b) {
        redSecondaryChannelOption.setState(r);
        greenSecondaryChannelOption.setState(g);
        blueSecondaryChannelOption.setState(b);
    }

    public void setCellCenterRequest(Boolean b) {
        cellCenterMarkerOption.setState(b);
        setMarkerSizeEnable();
    }

    public void setWorkingStackRequest(Boolean b) {
        workingStackCheckbox.setState(b);
    }

    public boolean getExtendedNeighborhoodOption() {
        return extendedNeighborhoodOption.getState();
    }

    public boolean getSecondaryStackOption() {
        return secondaryStackCheckbox.getState();
    }

    public Boolean[] getChannelOptions() {
        return new Boolean[]{redChannelOption.getState(), greenChannelOption.getState(), blueChannelOption.getState()};
    }
    
    public Boolean[] getSecondaryChannelOptions() {
        return new Boolean[]{redSecondaryChannelOption.getState(), greenSecondaryChannelOption.getState(), blueSecondaryChannelOption.getState()};
    }
    public boolean getBOAIntensityNormalize() {
        return boaIntensityNormalizeCheckbox.getState();
    }

    public boolean getPlateauOption() {
        return plateauOption.getState();
    }

    public Integer getMarkerRadius() {
        return new Integer(markerRadiusTextField.getText());
    }

    public void setSecondaryStackOption(Boolean b) {
        secondaryStackCheckbox.setState(b);
        setSecondaryStackChannelsEnable();
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

    private void setMarkerSizeEnable() {
        if (cellCenterMarkerOption.getState() == true) {
            markerRadiusTextField.setEnabled(true);
        } else {
            markerRadiusTextField.setEnabled(false);
        }
    }

    private void setBOAIntensityNormalizeEnable() {
        if (boaIntensityColourizeCheckbox.getState() == true) {
            boaIntensityNormalizeCheckbox.setEnabled(true);
        } else {
            boaIntensityNormalizeCheckbox.setEnabled(false);
            boaIntensityNormalizeCheckbox.setState(false);
        }
    }

    private void setSecondaryStackChannelsEnable() {
        if (secondaryStackCheckbox.getState() == true) {
            redSecondaryChannelOption.setEnabled(true);
            greenSecondaryChannelOption.setEnabled(true);
            blueSecondaryChannelOption.setEnabled(true);
//            redSecondaryChannelOption.setState(true);
//            greenSecondaryChannelOption.setState(true);
//            blueSecondaryChannelOption.setState(true);
        } else {
            redSecondaryChannelOption.setEnabled(false);
            greenSecondaryChannelOption.setEnabled(false);
            blueSecondaryChannelOption.setEnabled(false);
//            redSecondaryChannelOption.setState(false);
//            greenSecondaryChannelOption.setState(false);
//            blueSecondaryChannelOption.setState(false);
        }
    }
    
    private void checkIntensityChannelCheckboxStateChanged(java.awt.Checkbox box) {
        if (!(redChannelOption.getState() || blueChannelOption.getState() || greenChannelOption.getState())) {
            box.setState(true);
        }
    }
    
    private void checkSecondaryIntensityChannelCheckboxStateChanged(java.awt.Checkbox box) {
        if (!(redSecondaryChannelOption.getState() || blueSecondaryChannelOption.getState() || greenSecondaryChannelOption.getState())) {
            box.setState(true);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainOptions = new java.awt.Label();
        extendedNeighborhoodOption = new java.awt.Checkbox();
        plateauOption = new java.awt.Checkbox();
        inputOptionsLabel = new java.awt.Label();
        channel_label = new java.awt.Label();
        channel_panel = new java.awt.Panel();
        redChannelOption = new java.awt.Checkbox();
        greenChannelOption = new java.awt.Checkbox();
        blueChannelOption = new java.awt.Checkbox();
        secondaryStackCheckbox = new java.awt.Checkbox();
        secondaryChannelLabel = new java.awt.Label();
        secondaryChannelPanel = new java.awt.Panel();
        redSecondaryChannelOption = new java.awt.Checkbox();
        greenSecondaryChannelOption = new java.awt.Checkbox();
        blueSecondaryChannelOption = new java.awt.Checkbox();
        outputOptionsLabel = new java.awt.Label();
        markerPanel = new java.awt.Panel();
        cellCenterMarkerOption = new java.awt.Checkbox();
        markerRadiusTextField = new java.awt.TextField();
        boaColourizeCheckbox = new java.awt.Checkbox();
        boaIntensityColourizeCheckbox = new java.awt.Checkbox();
        boaIntensityNormalizeCheckbox = new java.awt.Checkbox();
        workingStackCheckbox = new java.awt.Checkbox();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new java.awt.GridBagLayout());

        mainOptions.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        mainOptions.setText("Gradient descent options:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(mainOptions, gridBagConstraints);

        extendedNeighborhoodOption.setLabel("Use extended (box) neighborhood.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        add(extendedNeighborhoodOption, gridBagConstraints);

        plateauOption.setLabel("Mark intensity plateau with single maximum.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        add(plateauOption, gridBagConstraints);

        inputOptionsLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        inputOptionsLabel.setText("Input options:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(inputOptionsLabel, gridBagConstraints);

        channel_label.setText("Choose channels for gradient descent:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        add(channel_label, gridBagConstraints);
        channel_label.getAccessibleContext().setAccessibleDescription("");

        channel_panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 1));

        redChannelOption.setLabel("red");
        redChannelOption.setName(""); // NOI18N
        redChannelOption.setState(true);
        redChannelOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                channelOptionItemStateChanged(evt);
            }
        });
        channel_panel.add(redChannelOption);

        greenChannelOption.setLabel("green");
        greenChannelOption.setState(true);
        greenChannelOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                channelOptionItemStateChanged(evt);
            }
        });
        channel_panel.add(greenChannelOption);

        blueChannelOption.setLabel("blue");
        blueChannelOption.setName(""); // NOI18N
        blueChannelOption.setState(true);
        blueChannelOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                channelOptionItemStateChanged(evt);
            }
        });
        channel_panel.add(blueChannelOption);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 14);
        add(channel_panel, gridBagConstraints);

        secondaryStackCheckbox.setLabel("Use secondary stack to measure intensity levels.");
        secondaryStackCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                secondaryStackCheckboxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        add(secondaryStackCheckbox, gridBagConstraints);

        secondaryChannelLabel.setText("Choose channels for intensity measurement:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        add(secondaryChannelLabel, gridBagConstraints);

        redSecondaryChannelOption.setEnabled(false);
        redSecondaryChannelOption.setLabel("red");
        redSecondaryChannelOption.setState(true);
        redSecondaryChannelOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                secondaryChannelOptionItemStateChanged(evt);
            }
        });
        secondaryChannelPanel.add(redSecondaryChannelOption);

        greenSecondaryChannelOption.setEnabled(false);
        greenSecondaryChannelOption.setLabel("green");
        greenSecondaryChannelOption.setState(true);
        greenSecondaryChannelOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                secondaryChannelOptionItemStateChanged(evt);
            }
        });
        secondaryChannelPanel.add(greenSecondaryChannelOption);

        blueSecondaryChannelOption.setEnabled(false);
        blueSecondaryChannelOption.setLabel("blue");
        blueSecondaryChannelOption.setState(true);
        blueSecondaryChannelOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                secondaryChannelOptionItemStateChanged(evt);
            }
        });
        secondaryChannelPanel.add(blueSecondaryChannelOption);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 14, 0, 14);
        add(secondaryChannelPanel, gridBagConstraints);

        outputOptionsLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        outputOptionsLabel.setText("Output options:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(outputOptionsLabel, gridBagConstraints);

        cellCenterMarkerOption.setLabel("Mark cell centers. Marker pixel radius:");
        cellCenterMarkerOption.setState(true);
        cellCenterMarkerOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cellCenterMarkerOptionItemStateChanged(evt);
            }
        });
        markerPanel.add(cellCenterMarkerOption);

        markerRadiusTextField.setColumns(1);
        markerRadiusTextField.setText("1");
        markerPanel.add(markerRadiusTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        add(markerPanel, gridBagConstraints);
        markerPanel.getAccessibleContext().setAccessibleName("");
        markerPanel.getAccessibleContext().setAccessibleDescription("");

        boaColourizeCheckbox.setLabel("Display basins of attractions (BOA).");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        add(boaColourizeCheckbox, gridBagConstraints);

        boaIntensityColourizeCheckbox.setLabel("Display basins of attractions according to measured intensity.");
        boaIntensityColourizeCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                boaIntensityColourizeCheckboxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        add(boaIntensityColourizeCheckbox, gridBagConstraints);

        boaIntensityNormalizeCheckbox.setLabel("Normalize intensites.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 30);
        add(boaIntensityNormalizeCheckbox, gridBagConstraints);

        workingStackCheckbox.setLabel("Display internal working stack.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        add(workingStackCheckbox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cellCenterMarkerOptionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cellCenterMarkerOptionItemStateChanged
        setMarkerSizeEnable();
    }//GEN-LAST:event_cellCenterMarkerOptionItemStateChanged

    private void boaIntensityColourizeCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_boaIntensityColourizeCheckboxItemStateChanged
        setBOAIntensityNormalizeEnable();
    }//GEN-LAST:event_boaIntensityColourizeCheckboxItemStateChanged

    private void channelOptionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_channelOptionItemStateChanged
        checkIntensityChannelCheckboxStateChanged((java.awt.Checkbox) evt.getItemSelectable());
    }//GEN-LAST:event_channelOptionItemStateChanged

    private void secondaryChannelOptionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_secondaryChannelOptionItemStateChanged
        checkSecondaryIntensityChannelCheckboxStateChanged((java.awt.Checkbox) evt.getItemSelectable());
    }//GEN-LAST:event_secondaryChannelOptionItemStateChanged

    private void secondaryStackCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_secondaryStackCheckboxItemStateChanged
        setSecondaryStackChannelsEnable();
    }//GEN-LAST:event_secondaryStackCheckboxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Checkbox blueChannelOption;
    private java.awt.Checkbox blueSecondaryChannelOption;
    private java.awt.Checkbox boaColourizeCheckbox;
    private java.awt.Checkbox boaIntensityColourizeCheckbox;
    private java.awt.Checkbox boaIntensityNormalizeCheckbox;
    private java.awt.Checkbox cellCenterMarkerOption;
    private java.awt.Label channel_label;
    private java.awt.Panel channel_panel;
    private java.awt.Checkbox extendedNeighborhoodOption;
    private java.awt.Checkbox greenChannelOption;
    private java.awt.Checkbox greenSecondaryChannelOption;
    private java.awt.Label inputOptionsLabel;
    private java.awt.Label mainOptions;
    private java.awt.Panel markerPanel;
    private java.awt.TextField markerRadiusTextField;
    private java.awt.Label outputOptionsLabel;
    private java.awt.Checkbox plateauOption;
    private java.awt.Checkbox redChannelOption;
    private java.awt.Checkbox redSecondaryChannelOption;
    private java.awt.Label secondaryChannelLabel;
    private java.awt.Panel secondaryChannelPanel;
    private java.awt.Checkbox secondaryStackCheckbox;
    private java.awt.Checkbox workingStackCheckbox;
    // End of variables declaration//GEN-END:variables
}
