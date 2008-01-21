
import costanza.Job;
import costanza.Options;
import costanza.Queue;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFrame extends java.awt.Frame {

	void askForScale(ij.measure.Calibration calibration) {
		scaleOptionPanel.setCalibration(calibration);
		setActivePanel(MainFrame.PanelId.SCALE);
	}

	void askForSecondaryStack() {
		setActivePanel(MainFrame.PanelId.SECONDARY_STACK);
	}

	void scaleOptionPanelContinueButtonPressed() {
		cardLayout.show(panel, "IOOptionPanel");
		update();
		plugin.scaleOptionPanelContinueButtonPressed();
	}

	void scaleOptionPanelCancelButtonPressed() {
		cardLayout.show(panel, "IOOptionPanel");
		update();
	}

	void secondaryStackOptionPanelContinueButtonPressed() throws Exception {
		cardLayout.show(panel, "IOOptionPanel");
		update();
		plugin.secondaryStackOptionPanelContinueButtonPressed();
	}

	void secondaryStackOptionPanelCancelButtonPressed() {
		cardLayout.show(panel, "IOOptionPanel");
		update();
	}

	private enum PanelId {

		IO,
		PRE_PROCESSING,
		POST_PROCESSING,
		SECONDARY_STACK,
		SCALE
	}
	private IOOptionPanel ioOptionPanel;
	private ProcessorOptionPanel preProcessorOptionPanel;
	private ProcessorOptionPanel postProcessorOptionPanel;
	private SecondaryStackOptionPanel secondaryStackOptionPanel;
	private ScaleOptionPanel scaleOptionPanel;
	private Costanza_Plugin plugin;
	private java.awt.CardLayout cardLayout;
	private Font menuFont;
	private Font font;
	private Color backgroundColor;

	public MainFrame(Costanza_Plugin plugin) throws Exception {
		menuFont = ij.Menus.getFont();
		font = ij.ImageJ.SansSerif12;
		backgroundColor = ij.ImageJ.backgroundColor;

		initComponents();
		this.plugin = plugin;
		initOptionPanels();
		setActivePanel(PanelId.IO);
		pack();
	}

	public int getResultRequest() {
		return ioOptionPanel.getResultRequest();
	}

	private void initOptionPanels() throws Exception {
		cardLayout = new CardLayout();
		panel.setLayout(cardLayout);

		ioOptionPanel = new IOOptionPanel(this);
		panel.add(ioOptionPanel, "IOOptionPanel");

		preProcessorOptionPanel = new ProcessorOptionPanel(this);

		preProcessorOptionPanel.addProcessorOptionToMenu("Invert image", InvertOption.class);
		preProcessorOptionPanel.addProcessorOptionToMenu("Smoothing", MeanFilterOption.class);
		preProcessorOptionPanel.addProcessorOptionToMenu("Background extraction", BackGroundFinderIntensityOption.class);
		preProcessorOptionPanel.addOptionPanel("Invert image");
		preProcessorOptionPanel.addOptionPanel("Background extraction");
		preProcessorOptionPanel.addOptionPanel("Smoothing");
		panel.add(preProcessorOptionPanel, "PreProcessorOptionPanel");

		postProcessorOptionPanel = new ProcessorOptionPanel(this);
		postProcessorOptionPanel.addProcessorOptionToMenu("Peak remover", PeakRemoverOption.class);
		postProcessorOptionPanel.addProcessorOptionToMenu("Peak merger", PeakMergerOption.class);
		postProcessorOptionPanel.addOptionPanel("Peak remover");
		postProcessorOptionPanel.addOptionPanel("Peak merger");
		panel.add(postProcessorOptionPanel, "PostProcessorOptionPanel");

		secondaryStackOptionPanel = new SecondaryStackOptionPanel(this);
		panel.add(secondaryStackOptionPanel, "SecondaryStackOptionPanel");

		scaleOptionPanel = new ScaleOptionPanel(this);
		panel.add(scaleOptionPanel, "ScaleOptionPanel");

		cardLayout.show(panel, "IOOptionPanel");
	}

	public void update() {
		pack();
		repaint();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new java.awt.Panel();
        menuBar = new java.awt.MenuBar();
        mainMenu = new java.awt.Menu();
        startMenuItem = new java.awt.MenuItem();
        quitMenuItem = new java.awt.MenuItem();
        optionsMenu = new java.awt.Menu();
        ioMenuItem = new java.awt.MenuItem();
        preProcessorMenuItem = new java.awt.MenuItem();
        postProcessorMenuItem = new java.awt.MenuItem();
        helpMenu = new java.awt.Menu();
        websiteMenuItem = new java.awt.MenuItem();
        documentationMenuItem = new java.awt.MenuItem();

        setBackground(backgroundColor);
        setName("Costanza Plugin"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        panel.setFont(font);
        panel.setLayout(new java.awt.CardLayout());
        add(panel, java.awt.BorderLayout.CENTER);

        menuBar.setFont(menuFont);

        mainMenu.setLabel("Main");

        startMenuItem.setLabel("Start analyze");
        startMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startMenuItemActionPerformed(evt);
            }
        });
        mainMenu.add(startMenuItem);

        quitMenuItem.setLabel("Close plugin");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        mainMenu.add(quitMenuItem);

        menuBar.add(mainMenu);

        optionsMenu.setLabel("Options");

        ioMenuItem.setLabel("Input and output");
        ioMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ioMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(ioMenuItem);

        preProcessorMenuItem.setLabel("Pre-processing");
        preProcessorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preProcessorMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(preProcessorMenuItem);

        postProcessorMenuItem.setLabel("Post-processing");
        postProcessorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postProcessorMenuItemActionPerformed(evt);
            }
        });
        optionsMenu.add(postProcessorMenuItem);

        menuBar.add(optionsMenu);

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
	/** Exit the Application */
	private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
		plugin.stop();
	}//GEN-LAST:event_exitForm

	private void ioMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ioMenuItemActionPerformed
		setActivePanel(MainFrame.PanelId.IO);
	}//GEN-LAST:event_ioMenuItemActionPerformed

	private void preProcessorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preProcessorMenuItemActionPerformed
		setActivePanel(MainFrame.PanelId.PRE_PROCESSING);
	}//GEN-LAST:event_preProcessorMenuItemActionPerformed

	private void postProcessorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postProcessorMenuItemActionPerformed
		setActivePanel(MainFrame.PanelId.POST_PROCESSING);
	}//GEN-LAST:event_postProcessorMenuItemActionPerformed

	private void startMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMenuItemActionPerformed
		Queue jobs = new Queue();
		try {
			preProcessorOptionPanel.addJobs(jobs);
			Options gradientDescentOption = new Options();
			gradientDescentOption.addOption("extendedNeighborhood", new Integer("0"));
			jobs.addJob(new Job("gradientdescent", gradientDescentOption));
			postProcessorOptionPanel.addJobs(jobs);
			Options intensityFinderOption = new Options();
			jobs.addJob(new Job("intensityfinder", intensityFinderOption));
		} catch (Exception exception) {
			Costanza_Plugin.printExceptionMessage(exception);
		}

		plugin.start(jobs, ioOptionPanel.getSecondaryStackOption());
	}//GEN-LAST:event_startMenuItemActionPerformed

	private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
		plugin.stop();
	}//GEN-LAST:event_quitMenuItemActionPerformed

	private void websiteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_websiteMenuItemActionPerformed
		try {
			ij.plugin.BrowserLauncher.openURL("http://cbbp.thep.lu.se");//GEN-LAST:event_websiteMenuItemActionPerformed
		} catch (IOException ex) {
			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
}

	private void documentationMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_documentationMenuItemActionPerformed
		try {
			ij.plugin.BrowserLauncher.openURL("http://cbbp.thep.lu.se");//GEN-LAST:event_documentationMenuItemActionPerformed
		} catch (IOException ex) {
			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void setActivePanel(PanelId id) {
		switch (id) {
			case IO:
				cardLayout.show(panel, "IOOptionPanel");
				update();
				break;
			case PRE_PROCESSING:
				cardLayout.show(panel, "PreProcessorOptionPanel");
				update();
				break;
			case POST_PROCESSING:
				cardLayout.show(panel, "PostProcessorOptionPanel");
				update();
				break;
			case SECONDARY_STACK:
				cardLayout.show(panel, "SecondaryStackOptionPanel");
				update();
				break;
			case SCALE:
				cardLayout.show(panel, "ScaleOptionPanel");
				update();
				break;
			default:
				ij.IJ.showMessage("Unexpected error in setActivePanel()");
		}
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.MenuItem documentationMenuItem;
    private java.awt.Menu helpMenu;
    private java.awt.MenuItem ioMenuItem;
    private java.awt.Menu mainMenu;
    private java.awt.MenuBar menuBar;
    private java.awt.Menu optionsMenu;
    private java.awt.Panel panel;
    private java.awt.MenuItem postProcessorMenuItem;
    private java.awt.MenuItem preProcessorMenuItem;
    private java.awt.MenuItem quitMenuItem;
    private java.awt.MenuItem startMenuItem;
    private java.awt.MenuItem websiteMenuItem;
    // End of variables declaration//GEN-END:variables
}
