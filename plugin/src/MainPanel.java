import java.awt.Button;
import java.awt.Checkbox;

public class MainPanel extends java.awt.Panel {
    Checkbox invertCheckbox;
    Button cancelButton;
    Button startButton;
    
    public MainPanel(Costanza_Plugin plugin) {
        // Add invert checkbox.
        invertCheckbox = new Checkbox("Invert image before processing.");
        this.add(invertCheckbox);
        
        // Add Cancel button.
        cancelButton = new Button("Cancel");
        cancelButton.addActionListener(plugin);
        this.add(cancelButton);                

        // Add Start button.
        startButton = new Button("Start");
        startButton.addActionListener(plugin);
        this.add(startButton);
    }
    
    public boolean getInvertCheckboxState() {
        return invertCheckbox.getState();
    }
}
