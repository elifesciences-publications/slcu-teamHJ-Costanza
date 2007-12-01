import Costanza.Case;
import Costanza.Driver;
import Costanza.Factory;
import Costanza.Image;
import Costanza.Job;
import Costanza.Options;
import Costanza.Queue;
import Costanza.Stack;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Costanza_Plugin implements PlugInFilter {
    private Factory factory;
    private ImagePlus imagePlus;
    
    public int setup(String arg, ImagePlus imagePlus) {
        initFactory();
        this.imagePlus = imagePlus;
        return DOES_ALL;
    }
    
    public void run(ImageProcessor imageProcessor) {
        imageProcessor.convertToFloat();
        ImageStack imageStack = imagePlus.getStack();
        int slices = imageStack.getSize();
        
        IJ.showMessage("Costanza", "Stack");
        
        Stack stack = new Stack();
        
        for (int n = 1; n <= slices; ++n) {
            ImageProcessor sliceProcessor = imageStack.getProcessor(n);
            int width = imageProcessor.getWidth();
            int height = imageProcessor.getHeight();
            
            Image image = new Image(width, height);
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    float value = sliceProcessor.getf(x, y);
                    image.setIntensity(x, y, value);
                }
            }
            try {
                stack.addImage(image);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        
        IJ.showMessage("Costanza", "Queue");
        
        
        Case IJCase = new Case(stack);
        Queue jobs = new Queue();
        try {
            jobs.addJob(new Job("invert", new Options()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        IJ.showMessage("Costanza", "Predriver");
        
        Driver driver = new Driver(jobs, IJCase, factory);
        
        driver.run();
        IJ.showMessage("Costanza", "Finished!");
    }
    
    private void initFactory() {
        factory = new Factory();
        factory.register("invert", Costanza.Inverter.class);
    }
}


