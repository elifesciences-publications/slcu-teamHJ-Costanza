import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Label;
import java.awt.TextField;

public class MainPanel extends java.awt.Panel {
    TextField meanFieldRadiusTextField;
    Checkbox invertCheckbox;
    Button cancelButton;
    Button startButton;
    
    public MainPanel(Costanza_Plugin plugin) {
        
        
        // Add MeanField radius text field.
        this.add(new Label("Mean field radius: "));
        meanFieldRadiusTextField = new TextField("5");
        
        this.add(meanFieldRadiusTextField);
        
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

    public float getMeanFieldRadiusValue() {
        Float value = new Float(meanFieldRadiusTextField.getText());
        return value.floatValue();
    }
}
