
import costanza.Image;
import costanza.Stack;

/** Utility class for the Costanza Plugin. */
public class Utility {

    /** Create new Stack object from ij.ImagePlus object. */
    static public Stack createStackFromImagePlus(ij.ImagePlus imagePlus) throws Exception {
	ij.ImageStack imageStack = imagePlus.getStack();
	int slices = imageStack.getSize();

	Stack stack = new Stack();
	for (int n = 1; n <= slices; ++n) {
	    ij.process.ImageProcessor sliceProcessor = imageStack.getProcessor(n);
	    Image image = new Image(sliceProcessor.createImage());
	    stack.addImage(image);
	}
	return stack;
    }

    /** Creates new ij.ImagePlus object from Stack. */
    static public ij.ImagePlus createImagePlusFromStack(Stack stack) throws Exception {
	int width = stack.getWidth();
	int height = stack.getHeight();

	ij.ImageStack is = new ij.ImageStack(width, height);

	for (int i = 0; i < stack.getDepth(); ++i) {
	    Image image = stack.getImage(i);
	    is.addSlice("", getImageProcessorFromImage(image));
	}
	return new ij.ImagePlus("Test Image", is);
    }

    /** Get ij.ImageProcessor from Image. */
    static public ij.process.ImageProcessor getImageProcessorFromImage(Image image) throws Exception {
	ij.ImagePlus ip = new ij.ImagePlus("", image.getImage());
	ij.ImageStack stack = ip.getImageStack();
	if (stack.getSize() != 1) {
	    throw new Exception("Unexpected error in Tools.getImageProcessorFromImage()");
	}
	return stack.getProcessor(1);
    }

    static public void printWarning(String string) {
	ij.IJ.showMessage("Costanza Plugin", "Warning: " + string + "\n");
    }

    static public void printError(String string) {
	ij.IJ.showMessage("Costanza Plugin", "Error: " + string + "\n");
    }
}
