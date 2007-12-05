public class MainFrame extends ij.plugin.frame.PlugInFrame {

	MainPanel panel;

	MainFrame(Costanza_Plugin plugin) {
		super("Costanza Plugin");
		panel = new MainPanel(plugin);
		add(panel);
		pack();
		setResizable(false);
	}

//    MainPanel getPanel() {
//	return panel;
	//  }
}
