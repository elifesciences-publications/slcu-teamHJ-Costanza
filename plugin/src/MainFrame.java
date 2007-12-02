import java.awt.Panel;

public class MainFrame extends ij.plugin.frame.PlugInFrame {
    private MainPanel panel;
    
    public MainFrame(Costanza_Plugin plugin) {
        super("Costanza Plugin");
        panel = new MainPanel(plugin);
        add(panel);
    }    
    
    public MainPanel getPanel() {
        return panel;
    }
}
