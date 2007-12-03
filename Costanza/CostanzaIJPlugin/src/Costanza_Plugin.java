import costanza.Case;
import costanza.Driver;
import costanza.Factory;
import costanza.Image;
import costanza.Inverter;
import costanza.Job;
import costanza.Options;
import costanza.Queue;
import costanza.Stack;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Float;

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
    
    
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        if (command.equalsIgnoreCase("Start")) {
            start();
        }
    }
    
    private void start() {
        try {
            ImagePlus imagePlus = IJ.getImage();
            Stack stack = Tools.createStackFromImagePlus(imagePlus);
            Case IJCase = new Case(stack);
            
            Queue jobs = new Queue();
            
            MainPanel panel = frame.getPanel();
            
            Float radius = new Float(panel.getMeanFieldRadiusValue());
            Options option = new Options();
            option.addOption("radius", radius);
            jobs.addJob(new Job("meanfield", option));
            
            if (panel.getInvertCheckboxState() == true) {
                jobs.addJob(new Job("invert", null));
            }
            
            Driver driver = new Driver(jobs, IJCase, factory);
            driver.run();
            
            Stack result = IJCase.getStack();
            int width = result.getWidth();
            int height = result.getHeight();
            
            ImageStack is = new ImageStack(width, height);
            
            for (int i = 0; i < result.getDepth(); ++i) {
                Image image = result.getImage(i);
                FloatProcessor fp = new FloatProcessor(image.getWidth(), image.getHeight());
                
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        float value = image.getIntensity(x, y) * 255.0f;
                        fp.setf(x, y, value);
                    }
                }
                is.addSlice("test", fp);
            }
            ImagePlus ip = new ImagePlus("Test Image", is);
            ip.show();
        } catch (Exception exception) {
            IJ.showMessage("Costanza Plugin", "Caught exception: " + exception.getMessage());
        }
    }
    
//        try {
//
//
//
//
//        } catch (Exception exception) {
//            error(exception.getMessage());
//        }
//
//    }
//
//
//
//
//
//    private void error(String message) {
//        IJ.showMessage("Error", "An error occured: " + message + "\n");
//    }
    
    
}


