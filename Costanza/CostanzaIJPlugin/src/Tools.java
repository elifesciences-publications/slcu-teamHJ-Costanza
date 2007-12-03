import costanza.Image;
import costanza.Stack;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class Tools {
    static public Stack createStackFromImagePlus(ImagePlus imagePlus) throws Exception {
        try {
            ImageStack imageStack = imagePlus.getStack();
            int slices = imageStack.getSize();
            
            Stack stack = new Stack();
            for (int n = 1; n <= slices; ++n) {
                ImageProcessor sliceProcessor = imageStack.getProcessor(n);
                ImageProcessor floatProcessor = sliceProcessor.convertToFloat();
                
                Image image = getFloatImageFromImageProcessor(floatProcessor);
                
                stack.addImage(image);
            }
            return stack;
        } catch (Exception exception) {
            throw exception;
        }
    }
    
    static public Image getFloatImageFromImageProcessor(ImageProcessor imageProcessor) {
        int width = imageProcessor.getWidth();
        int height = imageProcessor.getHeight();
        
        Image image = new Image(width, height);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float value = imageProcessor.getf(x, y) / 255.0f;
                image.setIntensity(x, y, value);
            }
        }
        return image;
    }
}
