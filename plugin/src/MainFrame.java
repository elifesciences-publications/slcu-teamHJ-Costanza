public class MainFrame extends ij.plugin.frame.PlugInFrame {
    public MainFrame(Costanza_Plugin plugin) {
        super("Costanza Plugin");
        add(new MainPanel(plugin));
    }    
}
