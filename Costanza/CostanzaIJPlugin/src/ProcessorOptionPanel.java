
import costanza.Factory;
import costanza.Queue;
import java.awt.GridBagConstraints;
import java.util.logging.Level;
import java.util.logging.Logger;

//public class ProcessorOptionPanel extends java.awt.Panel {
    public class ProcessorOptionPanel extends java.awt.Panel {

	private java.util.LinkedList<OptionPanel> optionPanelList;
	private MainFrame frame;
	private Factory<ProcessorOption> factory;

	public ProcessorOptionPanel(MainFrame frame) {
		this.frame = frame;
		factory = new Factory<ProcessorOption>();
		optionPanelList = new java.util.LinkedList<OptionPanel>();
		initComponents();
                setSize(new java.awt.Dimension(450, 450)); 
                setPreferredSize(new java.awt.Dimension(450, 450));
                scrollPane1.setSize(new java.awt.Dimension(300, 250)); 
                scrollPane1.setPreferredSize(new java.awt.Dimension(300, 250));
	}

        public MainFrame getFrame(){
            return frame;
        }
        
	public void addProcessorOptionToMenu(String name, Class<? extends ProcessorOption> processorOptionClass) {
		processorOptionChoice.add(name);
		factory.register(name, processorOptionClass);
	}

	public void addJobs(Queue jobs) throws Exception {
		java.util.Iterator<OptionPanel> iterator = optionPanelList.iterator();

		while (iterator.hasNext()) {
			OptionPanel optionPanel = iterator.next();
			ProcessorOption processorOption = optionPanel.getProcessorOption();
			processorOption.addJobs(jobs);
		}
	}

        public boolean hasOptionInMenu(String name){
            return factory.hasItem(name);
        }
        
        public java.util.Iterator<OptionPanel> getOptionIterator(){
            return optionPanelList.iterator();
        }
                
	public void removeOption(OptionPanel optionPanel) {
		displayPanel.remove(optionPanel);
		optionPanelList.remove(optionPanel);
		frame.update();
	}
        
        public void removeAllOptions() {
		displayPanel.removeAll();
		optionPanelList.clear();
		frame.update();
	}
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        northPanel = new java.awt.Panel();
        addButton = new java.awt.Button();
        label2 = new java.awt.Label();
        processorOptionChoice = new java.awt.Choice();
        label1 = new java.awt.Label();
        scrollPane1 = new java.awt.ScrollPane();
        displayPanel = new java.awt.Panel();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new java.awt.BorderLayout());

        northPanel.setBackground(new java.awt.Color(245, 245, 245));
        northPanel.setLayout(new java.awt.GridBagLayout());

        addButton.setLabel("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        northPanel.add(addButton, gridBagConstraints);

        label2.setFont(new java.awt.Font("Dialog", 1, 12));
        label2.setText("Processor queue:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        northPanel.add(label2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        northPanel.add(processorOptionChoice, gridBagConstraints);

        label1.setFont(new java.awt.Font("Dialog", 1, 12));
        label1.setText("Add a processor:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        northPanel.add(label1, gridBagConstraints);

        scrollPane1.setBackground(new java.awt.Color(250, 250, 250));

        displayPanel.setLayout(new java.awt.GridBagLayout());
        scrollPane1.add(displayPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        northPanel.add(scrollPane1, gridBagConstraints);
        scrollPane1.getAccessibleContext().setAccessibleParent(this);

        add(northPanel, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents
	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
		try {
			String name = processorOptionChoice.getSelectedItem();
			addOptionPanel(name);
		} catch (Exception ex) {
			Logger.getLogger(ProcessorOptionPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}//GEN-LAST:event_addButtonActionPerformed

	public void addOptionPanel(String name) throws Exception {
		OptionPanel optionPanel = new OptionPanel(this, factory.create(name));
		java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		displayPanel.add(optionPanel, gridBagConstraints);
		optionPanelList.add(optionPanel);
		frame.update();
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button addButton;
    private java.awt.Panel displayPanel;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Panel northPanel;
    private java.awt.Choice processorOptionChoice;
    private java.awt.ScrollPane scrollPane1;
    // End of variables declaration//GEN-END:variables
}
