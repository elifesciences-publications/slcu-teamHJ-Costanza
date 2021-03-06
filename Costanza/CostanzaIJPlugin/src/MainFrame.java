
import costanza.Job;
import costanza.Options;
import costanza.Queue;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class MainFrame extends java.awt.Frame {

    void setMenuAndButtonsEnabled(boolean arg) {
        startButton.setEnabled(arg);
    }

    void askForScale(ij.measure.Calibration calibration) {
        scaleOptionPanel.setCalibration(calibration);
    }

    void askForSecondaryStack() {
        SecondaryStackOptionDialog d = new SecondaryStackOptionDialog(this);
        d.setVisible(true);
    }

    void secondaryStackOptionPanelContinueButtonPressed() throws Exception {
        plugin.secondaryStackOptionPanelContinueButtonPressed();
    }

    void secondaryStackOptionPanelCancelButtonPressed() throws Exception {
        plugin.secondaryStackOptionPanelCanceleButtonPressed();
    }

    private IOOptionPanel ioOptionPanel;
    private ProcessorOptionPanel preProcessorOptionPanel;
    private ProcessorOptionPanel postProcessorOptionPanel;
    private ScaleOptionPanel scaleOptionPanel;
    private ConfigurationFileManager fc;
//    private BOAFileWriter bw;

    private Costanza_Plugin plugin;
    private Font menuFont;
    private Font font;
    private Color backgroundColor;

    public MainFrame(Costanza_Plugin plugin) throws Exception {
        super("Costanza");
        menuFont = ij.Menus.getFont();
        font = ij.ImageJ.SansSerif12;
        backgroundColor = ij.ImageJ.backgroundColor;

        initComponents();
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                synchronized (MainFrame.this) {
                    setProgress(0);
                    setIndeterminate(true);
                }
            }
        });

        this.plugin = plugin;
        initOptionPanels();
        fc = new ConfigurationFileManager(this);
//        bw = new BOAFileWriter(plugin);
//        jProgressBar1.setSize(new java.awt.Dimension(200, 20));
//        setSize(new java.awt.Dimension(350, 400));
        pack();
    }

    public String getProperty(String p) {
        return fc.getProperty(p);
    }

    public void setProgress(int i) {
        jProgressBar1.setValue(i);
    }

    public int getProgress() {
        return jProgressBar1.getValue();
    }

    public void setIndeterminate(boolean b) {
        jProgressBar1.setIndeterminate(b);
    }

    Font getFrameFont() {
        return font;
    }

    public int getResultRequest() {
        return ioOptionPanel.getResultRequest();
    }
            
    public int getStoredResultRequest() {
        return fc.getResultRequest();
    }

    public IOOptionPanel getIOPanel() {
        return ioOptionPanel;
    }

    public java.awt.TextField getProgressTextField() {
        return textField1;
    }

    public void setProgressTextField(String s) {
        textField1.setText(s);
    }

    public void setEnabledOptions(boolean b) {
        ioOptionPanel.setEnabled(b);
        preProcessorOptionPanel.setEnabled(b);
        postProcessorOptionPanel.setEnabled(b);
        scaleOptionPanel.setEnabled(b);
    }

    public ProcessorOptionPanel getPreProcessorPanel() {
        return preProcessorOptionPanel;
    }

    public ProcessorOptionPanel getPostProcessorPanel() {
        return postProcessorOptionPanel;
    }

    public ScaleOptionPanel getScaleOptionPanel() {
        return scaleOptionPanel;
    }

    public int getIntensityLevelsNumber() {
        return new Integer(scaleOptionPanel.getIntensityLevels());
    }

    public void setIntensityLevelsNumber(Integer i) {
        scaleOptionPanel.setIntensityLevels(i);
    }

    private void initOptionPanels() throws Exception {
//        jTabbedPane1.setPreferredSize(new java.awt.Dimension(450, 380));
        ioOptionPanel = new IOOptionPanel(this);
        jTabbedPane1.addTab("general", ioOptionPanel);

        preProcessorOptionPanel = new ProcessorOptionPanel(this);

        preProcessorOptionPanel.addProcessorOptionToMenu(InvertOption.NAME, InvertOption.class);
        preProcessorOptionPanel.addProcessorOptionToMenu(MeanFilterOption.NAME, MeanFilterOption.class);
        preProcessorOptionPanel.addProcessorOptionToMenu(MedianFilterOption.NAME, MedianFilterOption.class);
        preProcessorOptionPanel.addProcessorOptionToMenu(BackGroundFinderIntensityOption.NAME, BackGroundFinderIntensityOption.class);
        preProcessorOptionPanel.addOptionPanel(BackGroundFinderIntensityOption.NAME);
        preProcessorOptionPanel.addOptionPanel(MeanFilterOption.NAME);

        jTabbedPane1.addTab("pre-processor", preProcessorOptionPanel);
        postProcessorOptionPanel = new ProcessorOptionPanel(this);
        postProcessorOptionPanel.addProcessorOptionToMenu(PeakRemoverOption.NAME, PeakRemoverOption.class);
        postProcessorOptionPanel.addProcessorOptionToMenu(PeakMergerOption.NAME, PeakMergerOption.class);
        postProcessorOptionPanel.addOptionPanel(PeakRemoverOption.NAME);
        postProcessorOptionPanel.addOptionPanel(PeakMergerOption.NAME);

        jTabbedPane1.addTab("post-processor", postProcessorOptionPanel);
//		secondaryStackOptionPanel = new SecondaryStackOptionPanel(this);

        scaleOptionPanel = new ScaleOptionPanel(this);

        jTabbedPane1.addTab("scaling", scaleOptionPanel);
        jTabbedPane1.setFont(font);
    }

    public void update() {
        pack();
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonPanel = new java.awt.Panel();
        startButton = new java.awt.Button();
        textField1 = new java.awt.TextField();
        jProgressBar1 = new javax.swing.JProgressBar();
        cancelButton = new java.awt.Button();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        menuBar = new java.awt.MenuBar();
        fileMenu = new java.awt.Menu();
        openFile = new java.awt.MenuItem();
        saveFile = new java.awt.MenuItem();
        saveBoas = new java.awt.MenuItem();
        read_ALT_BOA_tif = new java.awt.MenuItem();
        write_ALT_BOA_tif = new java.awt.MenuItem();
        quitMenuItem = new java.awt.MenuItem();
        viewMenu = new java.awt.Menu();
        viewResult = new java.awt.MenuItem();
        helpMenu = new java.awt.Menu();
        websiteMenuItem = new java.awt.MenuItem();
        documentationMenuItem = new java.awt.MenuItem();

        setBackground(backgroundColor);
        setMinimumSize(new java.awt.Dimension(350, 100));
        setName("Costanza Plugin"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        startButton.setLabel("Start analysis");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(startButton);

        textField1.setBackground(new java.awt.Color(240, 240, 240));
        textField1.setVisible(false);
        buttonPanel.add(textField1);

        jProgressBar1.setDoubleBuffered(true);
        jProgressBar1.setPreferredSize(new java.awt.Dimension(200, 20));
        jProgressBar1.setStringPainted(true);
        buttonPanel.add(jProgressBar1);

        cancelButton.setLabel("Cancel analysis");
        cancelButton.setVisible(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setAutoscrolls(true);
        jTabbedPane1.setDoubleBuffered(true);
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(300, 300));
        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentResized(evt);
            }
        });
        add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        menuBar.setFont(menuFont);

        fileMenu.setLabel("File");

        openFile.setLabel("Load configuration");
        openFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openConfFile(evt);
            }
        });
        fileMenu.add(openFile);

        saveFile.setLabel("Save configuration");
        saveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigurationFile(evt);
            }
        });
        fileMenu.add(saveFile);

        saveBoas.setActionCommand("Save BOAs to file");
        saveBoas.setLabel("Save BOAs to file");
        saveBoas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBoasFile(evt);
            }
        });
        fileMenu.add(saveBoas);

        read_ALT_BOA_tif.setLabel("Read BOAs from tif file");
        read_ALT_BOA_tif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                read_ALT_BOA_tif(evt);
            }
        });
        fileMenu.add(read_ALT_BOA_tif);

        write_ALT_BOA_tif.setLabel("Write BOAs to tif file");
        write_ALT_BOA_tif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                write_ALT_BOA_tif(evt);
            }
        });
        fileMenu.add(write_ALT_BOA_tif);

        quitMenuItem.setLabel("Quit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitRequest(evt);
            }
        });
        fileMenu.add(quitMenuItem);

        menuBar.add(fileMenu);

        viewMenu.setActionCommand("View");
        viewMenu.setLabel("View");

        viewResult.setLabel("View result");
        viewResult.setName("");
        viewResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewResultActionPerformed(evt);
            }
        });
        viewMenu.add(viewResult);

        menuBar.add(viewMenu);

        helpMenu.setLabel("Help");

        websiteMenuItem.setLabel("Costanza's homepage");
        websiteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                websiteMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(websiteMenuItem);

        documentationMenuItem.setLabel("Online documentation");
        documentationMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                documentationMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(documentationMenuItem);

        menuBar.add(helpMenu);

        setMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents
	/**
     * Exit the Application
     */
	private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
            plugin.stop(Costanza_Plugin.PluginStatus.EXIT_APPLICATION);
	}//GEN-LAST:event_exitForm

	private void websiteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_websiteMenuItemActionPerformed
            try {
                ij.plugin.BrowserLauncher.openURL("http://cbbp.thep.lu.se/~henrik/Costanza/");
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
}//GEN-LAST:event_websiteMenuItemActionPerformed

	private void documentationMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_documentationMenuItemActionPerformed
            try {
                ij.plugin.BrowserLauncher.openURL("http://cbbp.thep.lu.se/~henrik/Costanza/doc/userguide.pdf");
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
	}//GEN-LAST:event_documentationMenuItemActionPerformed
	private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
            setMenuAndButtonsEnabled(false);
            setEnabledOptions(false);
            fc.retriveGUIProperties();
            boolean sso = ioOptionPanel.getSecondaryStackOption();
            setProgress(0);
            setIndeterminate(true);
            setProgressTextField("Initializing...");
            Queue jobs = new Queue();
            try {
                preProcessorOptionPanel.addJobs(jobs);
                Options gradientDescentOption = new Options();
                gradientDescentOption.addOption("useExtendedNeighborhood", new Boolean(ioOptionPanel.getExtendedNeighborhoodOption()));
                gradientDescentOption.addOption("usePlateau", new Boolean(ioOptionPanel.getPlateauOption()));
                gradientDescentOption.addOption("intensityLevelsNumber", getIntensityLevelsNumber());
                jobs.addJob(new Job("gradientdescent", gradientDescentOption));
                postProcessorOptionPanel.addJobs(jobs);
                Options intensityFinderOption = new Options();
                jobs.addJob(new Job("intensityfinder", intensityFinderOption));
            } catch (Exception exception) {
                Costanza_Plugin.printExceptionMessage(exception);
            }
            setEnabledOptions(true);
            plugin.start(jobs, sso);
	}//GEN-LAST:event_startButtonActionPerformed

        private void quitRequest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitRequest
            fc.saveProperties(new File(ConfigurationFileManager.LAST_FILE));
            plugin.stop(Costanza_Plugin.PluginStatus.EXIT_APPLICATION);
        }//GEN-LAST:event_quitRequest

        private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
            // TODO add your handling code here:
}//GEN-LAST:event_cancelButtonActionPerformed

        private void openConfFile(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openConfFile
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                fc.loadProperties(file);
            }
        }//GEN-LAST:event_openConfFile

        private void saveConfigurationFile(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigurationFile
            int returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                fc.saveProperties(file);
            }
        }//GEN-LAST:event_saveConfigurationFile

        private void jTabbedPane1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentResized
            doLayout();
        }//GEN-LAST:event_jTabbedPane1ComponentResized

        private void saveBoasFile(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBoasFile
//           System.out.println("saving boas: MainFrame");
            BOAFileWriter bw = new BOAFileWriter(plugin);
            int returnVal = bw.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = bw.getSelectedFile();
                bw.saveBOAs(file);
            }
        }//GEN-LAST:event_saveBoasFile

    private void read_ALT_BOA_tif(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_read_ALT_BOA_tif
//        System.out.println("read_ALT_BOA_tif: MainFrame");
        TiffFileReader tfr = new TiffFileReader();
        int returnVal = tfr.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = tfr.getSelectedFile();
            plugin.readBOAsFromTiff(file);
        }
    }//GEN-LAST:event_read_ALT_BOA_tif

    private void write_ALT_BOA_tif(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_write_ALT_BOA_tif
//        System.out.println("write_ALT_BOA_tif: MainFrame");
        TiffFileReader tfr = new TiffFileReader();
        int returnVal = tfr.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = tfr.getSelectedFile();
            plugin.writeBOAsToTiff(file);
        }

    }//GEN-LAST:event_write_ALT_BOA_tif

    private void viewResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewResultActionPerformed
//System.out.println("displaying results from stack: MainFrame");
        plugin.viewResult("Result image");
    }//GEN-LAST:event_viewResultActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Panel buttonPanel;
    private java.awt.Button cancelButton;
    private java.awt.MenuItem documentationMenuItem;
    private java.awt.Menu fileMenu;
    private java.awt.Menu helpMenu;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private java.awt.MenuBar menuBar;
    private java.awt.MenuItem openFile;
    private java.awt.MenuItem quitMenuItem;
    private java.awt.MenuItem read_ALT_BOA_tif;
    private java.awt.MenuItem saveBoas;
    private java.awt.MenuItem saveFile;
    private java.awt.Button startButton;
    private java.awt.TextField textField1;
    private java.awt.Menu viewMenu;
    private java.awt.MenuItem viewResult;
    private java.awt.MenuItem websiteMenuItem;
    private java.awt.MenuItem write_ALT_BOA_tif;
    // End of variables declaration//GEN-END:variables
}
