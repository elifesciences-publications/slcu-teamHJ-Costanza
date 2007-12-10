
import costanza.Job;
import costanza.Queue;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MainPanel extends java.awt.Panel {
	private ProcessorMenuPanel preProcessorMenuPanel;
	private Costanza_Plugin plugin;
	private java.awt.Choice mainChoice;
	private java.awt.Panel cardPanel;
	private java.awt.CardLayout cardLayout;
	private AlgorithmPanel algorithmPanel;

	public MainPanel(Costanza_Plugin plugin, MainFrame frame) {
		this.plugin = plugin;
		setLayout(new BorderLayout(10, 10));
		setBackground(new java.awt.Color(255, 255, 255));

		// Add Choice to MainPanel.
		mainChoice = new java.awt.Choice();
		mainChoice.add("Main options");
		mainChoice.add("Pre-processing options");
		mainChoice.add("Post-processing options");
		mainChoice.addItemListener(
				new ItemListener() {

					public void itemStateChanged(ItemEvent event) {
						menuChanged();
					}
				});
		add(mainChoice, java.awt.BorderLayout.NORTH);

		// Add CardLayout Panel.
		cardPanel = new java.awt.Panel();
		cardPanel.setLayout(cardLayout = new CardLayout());
		cardPanel.add(algorithmPanel = new AlgorithmPanel(this), "AlgorithmPanel");
		preProcessorMenuPanel = new ProcessorMenuPanel(frame);
		preProcessorMenuPanel.addProcessorOptionToMenu("Smoother", MeanFilterOption.class);
		cardPanel.add(preProcessorMenuPanel, "PreProcessingPanel");
		add(cardPanel, java.awt.BorderLayout.CENTER);

		// Add button Panel.
		java.awt.Panel buttonPanel = new java.awt.Panel();
		buttonPanel.setBackground(new java.awt.Color(255, 255, 255));
		
		java.awt.Button cancelButton = new java.awt.Button();
		java.awt.Button startButton = new java.awt.Button();
		
        cancelButton.setLabel("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonPressed();
            }
        });
		buttonPanel.add(cancelButton);

        startButton.setLabel("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonPressed();
            }
        });
        buttonPanel.add(startButton);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);
	}


	void addJobs(Queue jobs) throws Exception {
		preProcessorMenuPanel.addJobs(jobs);
		algorithmPanel.addInverterJob(jobs);
//		preProcessingPanel.addMeanFilterJob(jobs);
		jobs.addJob(new Job("gradientdescent", null));
//		postProcessingPanel.addPeakRemover(jobs);
//		postProcessingPanel.addPeakMerger(jobs);
	}

	private void cancelButtonPressed() {
		plugin.stop();
	}

	private void startButtonPressed() {
		plugin.start(this);
	}

	private void menuChanged() {
		String choice = mainChoice.getSelectedItem();
		if (choice.equalsIgnoreCase("Main options")) {
			cardLayout.show(cardPanel, "AlgorithmPanel");
		} else if (choice.equalsIgnoreCase("Pre-processing options")) {
			cardLayout.show(cardPanel, "PreProcessingPanel");
		} else if (choice.equalsIgnoreCase("Post-processing options")) {
			cardLayout.show(cardPanel, "PostProcessingPanel");
		} else {
			Utility.printError("Unexpected error in menuChanged()");
		}
	}
}
