
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MainPanel extends java.awt.Panel {

	private Costanza_Plugin plugin;
	private java.awt.Choice mainChoice;
	private java.awt.Panel cardPanel;
	private java.awt.CardLayout cardLayout;
	private AlgorithmPanel algorithmPanel;
	private PreProcessingPanel preProcessingPanel;
	private PostProcessingPanel postProcessingPanel;

	public MainPanel(Costanza_Plugin plugin) {
		this.plugin = plugin;
		setLayout(new BorderLayout(10, 10));

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
		cardPanel.add(preProcessingPanel = new PreProcessingPanel(this), "PreProcessingPanel");
		cardPanel.add(postProcessingPanel = new PostProcessingPanel(this), "PostProcessingPanel");
		add(cardPanel, java.awt.BorderLayout.SOUTH);
	}

	void cancelButtonPressed() {
		plugin.stop();
	}

	void startButtonPressed() {
		boolean invert = algorithmPanel.getInvertCheckboxState();
		plugin.start(5.0f, 1, invert);
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
