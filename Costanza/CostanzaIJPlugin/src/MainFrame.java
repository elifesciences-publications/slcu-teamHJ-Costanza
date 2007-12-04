public class MainFrame extends ij.plugin.frame.PlugInFrame {

	MainPanel panel;

	MainFrame(Costanza_Plugin plugin) {
		super("Costanza Plugin");
		panel = new MainPanel();
		add(panel);
		pack();
	}

//    MainPanel getPanel() {
//	return panel;
	//  }
}
