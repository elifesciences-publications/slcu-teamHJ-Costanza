import Costanza.Case;
import Costanza.Factory;
import Costanza.Image;
import Costanza.Inverter;
import Costanza.Options;
import Costanza.Stack;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
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
        factory.register("invert", Costanza.Inverter.class);
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
            
            Inverter inverter = new Inverter();
            inverter.process(IJCase, new Options());
            
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
////        Queue jobs = new Queue();
////        try {
////            jobs.addJob(new Job("invert", new Options()));
////        } catch (Exception exception) {
////            exception.printStackTrace();
////        }
//
////        Driver driver = new Driver(jobs, IJCase, factory);
////        driver.run();
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


