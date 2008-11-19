
import costanza.Job;
import costanza.Options;
import costanza.Queue;
import java.awt.Panel;

public class MedianFilterOption extends java.awt.Panel implements ProcessorOption {

    public static final String NAME = "Median Filter";
    public MedianFilterOption() {
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        radiusLabel = new java.awt.Label();
        radiusTextField = new java.awt.TextField();
        repeatLabel = new java.awt.Label();
        repeatTextField = new java.awt.TextField();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new java.awt.GridBagLayout());

        radiusLabel.setText("Radius:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(radiusLabel, gridBagConstraints);

        radiusTextField.setText("2.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(radiusTextField, gridBagConstraints);

        repeatLabel.setText("Number of times:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(repeatLabel, gridBagConstraints);

        repeatTextField.setText("2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(repeatTextField, gridBagConstraints);
    }// </editor-fold>
    // Variables declaration - do not modify
    private java.awt.Label radiusLabel;
    private java.awt.TextField radiusTextField;
    private java.awt.Label repeatLabel;
    private java.awt.TextField repeatTextField;
    // End of variables declaration
    public Panel getPanel() {
        return this;
    }

    public String getProcessorName() {
        return NAME;
    }

    public void addJobs(Queue jobs) throws Exception {
            jobs.addJob(new Job("medianfilter", getOptions()));
    }

    public Options getOptions() throws Exception {
        Options options = new Options();
        options.addOption("medianFilterRadius", new Float(radiusTextField.getText()));
        options.addOption("medianFilterRepeat", new Integer(repeatTextField.getText()));
        return options;
    }
    
    public void setFromOptions(Options o) throws Exception {
//        System.out.println(getProcessorName());
//        System.out.println(o.toString());
        Integer i = (Integer)o.getOptionValue("medianFilterRepeat");
        Float r = (Float)o.getOptionValue("medianFilterRadius");
        radiusTextField.setText(r.toString());
        repeatTextField.setText(i.toString());
    }
}
