import java.awt.Button;

public class MainPanel extends java.awt.Panel {
    public MainPanel(Costanza_Plugin plugin) {
        // Add Cancel button.
        Button cancelButton = new Button("Cancel");
        cancelButton.addActionListener(plugin);
        this.add(cancelButton);                

        // Add Start button.
        Button startButton = new Button("Start");
        startButton.addActionListener(plugin);
        this.add(startButton);
        
    }
}
