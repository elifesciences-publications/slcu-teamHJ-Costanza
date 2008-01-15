
import costanza.Job;
import costanza.Options;
import costanza.Queue;

public class MainPanel extends java.awt.Panel {

	private ProcessorMenuPanel preProcessorMenuPanel;
	private ProcessorMenuPanel postProcessorMenuPanel;
	private Costanza_Plugin plugin;
	private java.awt.Choice mainChoice;
	private java.awt.Panel cardPanel;
	private java.awt.CardLayout cardLayout;
	private AlgorithmPanel algorithmPanel;

	public MainPanel(Costanza_Plugin plugin, MainFrame frame) throws java.lang.Exception {
		this.plugin = plugin;
		setLayout(new java.awt.BorderLayout(10, 10));
		setBackground(new java.awt.Color(255, 255, 255));

		// Add Choice to MainPanel.
		mainChoice = new java.awt.Choice();
		mainChoice.add("Main options");
		mainChoice.add("Pre-processing options");
		mainChoice.add("Post-processing options");
		mainChoice.addItemListener(
				new java.awt.event.ItemListener() {

					public void itemStateChanged(java.awt.event.ItemEvent event) {
						menuChanged();
					}
				});
		add(mainChoice, java.awt.BorderLayout.NORTH);

		// Add CardLayout Panel.
		cardPanel = new java.awt.Panel();
		cardPanel.setLayout(cardLayout = new java.awt.CardLayout());
		cardPanel.add(algorithmPanel = new AlgorithmPanel(this), "AlgorithmPanel");
		preProcessorMenuPanel = new ProcessorMenuPanel(frame);
		preProcessorMenuPanel.addProcessorOptionToMenu("Smoothing", MeanFilterOption.class);
		preProcessorMenuPanel.addProcessorOptionToMenu("Background extraction", BackGroundFinderIntensityOption.class);
		preProcessorMenuPanel.addOptionPanel("Background extraction");
		preProcessorMenuPanel.addOptionPanel("Smoothing");
		cardPanel.add(preProcessorMenuPanel, "PreProcessingPanel");
		add(cardPanel, java.awt.BorderLayout.CENTER);
		postProcessorMenuPanel = new ProcessorMenuPanel(frame);
		postProcessorMenuPanel.addProcessorOptionToMenu("Peak remover", PeakRemoverOption.class);
		postProcessorMenuPanel.addProcessorOptionToMenu("Peak merger", PeakMergerOption.class);
		postProcessorMenuPanel.addOptionPanel("Peak remover");
		postProcessorMenuPanel.addOptionPanel("Peak merger");
		cardPanel.add(postProcessorMenuPanel, "PostProcessingPanel");



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
		Options gradientDescentOption = new Options();
		gradientDescentOption.addOption("extendedNeighborhood", new Integer("0"));
		jobs.addJob(new Job("gradientdescent", gradientDescentOption));
		postProcessorMenuPanel.addJobs(jobs);
		Options intensityFinderOption = new Options();
		jobs.addJob(new Job("intensityfinder", intensityFinderOption));
	}

	int getResultRequest() {
		return algorithmPanel.getResultRequest();
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
