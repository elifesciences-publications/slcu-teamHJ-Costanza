public class MainFrame extends ij.plugin.frame.PlugInFrame {

	MainPanel panel;

	MainFrame(Costanza_Plugin plugin) throws Exception {
		super("Costanza Plugin");
		panel = new MainPanel(plugin, this);
		add(panel);
		pack();
		setResizable(false);
	}

	void update() {
		pack();
		repaint();
	}
}
