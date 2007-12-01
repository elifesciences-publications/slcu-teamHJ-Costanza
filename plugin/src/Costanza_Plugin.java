import Costanza.Case;
import Costanza.Driver;
import Costanza.Factory;
import Costanza.Image;
import Costanza.Inverter;
import Costanza.Job;
import Costanza.Options;
import Costanza.Queue;
import Costanza.Stack;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.io.FileWriter;
import java.io.IOException;

public class Costanza_Plugin implements PlugInFilter {
    private Factory factory;
    private ImagePlus imagePlus;
    
    public int setup(String arg, ImagePlus imagePlus) {
        initFactory();
        this.imagePlus = imagePlus;
        return DOES_ALL;
    }
    
    public void run(ImageProcessor imageProcessor) {
        ImageStack imageStack = imagePlus.getStack();
        int slices = imageStack.getSize();
        
        Stack stack = new Stack();
        
        for (int n = 1; n <= slices; ++n) {
            ImageProcessor sliceProcessor = imageStack.getProcessor(n);
            ImageProcessor floatProcessor = sliceProcessor.convertToFloat();
            int width = floatProcessor.getWidth();
            int height = floatProcessor.getHeight();
            
            Image image = new Image(width, height);
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    float value = floatProcessor.getf(x, y) / 255.0f;
                    image.setIntensity(x, y, value);
                }
            }
            
            try {
                stack.addImage(image);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        
        
        Case IJCase = new Case(stack);
        
        IJ.showMessage("Costanza", "Step one finished!");
        
        Inverter inverter = new Inverter();
        try {
            inverter.process(IJCase, new Options());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        IJ.showMessage("Costanza", "Step two finished!");
        
//        Queue jobs = new Queue();
//        try {
//            jobs.addJob(new Job("invert", new Options()));
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
        
//        Driver driver = new Driver(jobs, IJCase, factory);
//        driver.run();
        
        
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
    }
    
    private void initFactory() {
        factory = new Factory();
        factory.register("invert", Costanza.Inverter.class);
    }
}


