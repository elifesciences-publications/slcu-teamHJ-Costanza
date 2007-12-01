package plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
        
public class Costanza_Plugin implements PlugInFilter {
    public void run(String arg) {
       
    }

    public int setup(String arg, ImagePlus imagePlus) {
        if (arg.equals("about")) {
            IJ.showMessage("Costanza", "Hello World!");
        }
        return DOES_ALL;
    }

    public void run(ImageProcessor imageProcessor) {
    }
}
