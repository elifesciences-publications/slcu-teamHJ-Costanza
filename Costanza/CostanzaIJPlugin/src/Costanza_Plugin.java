import costanza.Case;
import costanza.Driver;
import costanza.Factory;
import costanza.Job;
import costanza.Options;
import costanza.Queue;
import costanza.Stack;
import ij.IJ;
import ij.ImagePlus;

public class Costanza_Plugin implements ij.plugin.PlugIn  {
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
        
    private void start() {
        try {
            ImagePlus imagePlus = IJ.getImage();
            Stack stack = Tools.createStackFromImagePlus(imagePlus);
            Case IJCase = new Case(stack);
            
            Queue jobs = new Queue();
            
//     //       MainPanel panel = frame.getPanel();
////            float radius = panel.getMeanFieldRadiusValue();
//            Options option = new Options();
//            option.addOption("radius", new Float(radius));
//            jobs.addJob(new Job("meanfilter", option));
//            
//            if (panel.getInvertCheckboxState() == true) {
//                jobs.addJob(new Job("invert", null));
//            }
            
            Driver driver = new Driver(jobs, IJCase, factory);
            driver.run();
            
            Stack result = IJCase.getStack();

            ImagePlus ip = Tools.createImagePlusFromStack(result);
            ip.show();
        } catch (Exception exception) {
            IJ.showMessage("Costanza Plugin", "Caught exception: " + exception.getMessage());
        }
    }
}


