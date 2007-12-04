import costanza.Case;
import costanza.Driver;
import costanza.Factory;
import costanza.Job;
import costanza.Options;
import costanza.Queue;
import costanza.Stack;
import ij.IJ;
import ij.ImagePlus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Costanza_Plugin implements ij.plugin.PlugIn, ActionListener  {
    private Factory factory;
    private MainFrame frame;
    
    public void run(String arg) {
        initFactory();
        frame = new MainFrame(this);
        frame.setVisible(true);
    }
    
    private void initFactory() {
        factory = new Factory();
        factory.register("invert", costanza.Inverter.class);
        factory.register("meanfilter", costanza.MeanFilter.class);
    }
        
    public void start(float radius, int repeat, boolean invert) {
        try {
            ImagePlus imagePlus = IJ.getImage();
            Stack stack = Tools.createStackFromImagePlus(imagePlus);
            Case IJCase = new Case(stack);
            
            Queue jobs = new Queue();
            
            Options option = new Options();
            option.addOption("radius", new Float(radius));
			for (int i = 0; i < repeat; ++i) {
				jobs.addJob(new Job("meanfilter", option));				
			}
            
            if (invert == true) {
                jobs.addJob(new Job("invert", null));
            }
            
            Driver driver = new Driver(jobs, IJCase, factory);
            driver.run();
            
            Stack result = IJCase.getStack();

            ImagePlus ip = Tools.createImagePlusFromStack(result);
            ip.show();
        } catch (Exception exception) {
            IJ.showMessage("Costanza Plugin", "Caught exception: " + exception.getMessage());
        }
    }

	public void actionPerformed(ActionEvent e) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}


