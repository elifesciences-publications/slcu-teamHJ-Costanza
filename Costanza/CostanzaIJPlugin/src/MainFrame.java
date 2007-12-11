public class MainFrame extends ij.plugin.frame.PlugInFrame {

	MainPanel panel;

	MainFrame(Costanza_Plugin plugin) throws Exception {
		super("Costanza Plugin");
		panel = new MainPanel(plugin, this);
		add(panel);
		pack();
		setResizable(false);
	}

	void scaleDialogCancel(ScaleDialog aThis) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	void update() {
		pack();
		repaint();
	}
}
