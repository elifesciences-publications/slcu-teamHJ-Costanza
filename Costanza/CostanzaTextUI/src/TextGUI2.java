
import costanza.Image;
import costanza.Stack;
import costanza.Inverter;
import costanza.Case;
import costanza.Options;
import costanza.MeanFilter;
import costanza.GradientDescent;
import costanza.IntensityFinder;
import costanza.PeakMerger;
import costanza.PeakRemover;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * The Text version of our GUI.
 * @author michael
 */
public class TextGUI2 {

    /** Creates a new instance of TextGUI */
    public TextGUI2(String baseName) throws Exception {
	final int invertFlag = 1;
	final int meanFilterFlag = 1;
	final int gradientDescentFlag = 1;
	final int intensityFinderFlag = 1;
	final int peakMergerFlag = 0;
	final int peakRemoverFlag = 0;

	System.out.println("Creating a Stack");
	Stack stack = getImageStack(baseName);
	Case myCase = new Case(stack);

	if (invertFlag != 0) {
	    System.out.println("### Applying Invert ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    System.out.println("Creating Processor");
	    Inverter inverter = new Inverter();
	    System.out.println("Running Processor");
	    myCase = inverter.process(myCase, options);
	    System.out.println("Done!\n");
	}
	if (meanFilterFlag != 0) {
	    System.out.println("### Applying MeanFilter ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    options.addOption("radius", new Float(3));
	    System.out.println("Creating Processor");
	    MeanFilter meanFilter = new MeanFilter();
	    System.out.println("Running Processor");
	    myCase = meanFilter.process(myCase, options);
	    System.out.println("Done!\n");
	}
	if (gradientDescentFlag != 0) {
	    System.out.println("### Applying GradientDescent ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    System.out.println("Creating Processor");
	    GradientDescent gradientDescent = new GradientDescent();
	    System.out.println("Running Processor");
	    myCase = gradientDescent.process(myCase, options);
	    System.out.println("Done!\n");
	}
	if (intensityFinderFlag != 0) {
	    System.out.println("### Applying IntensityFinder ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    System.out.println("Creating Processor");
	    IntensityFinder intensityFinder = new IntensityFinder();
	    System.out.println("Running Processor");
	    myCase = intensityFinder.process(myCase, options);
	    System.out.println("Done!\n");
	}
	if (peakMergerFlag != 0) {
	    System.out.println("### Applying PeakMerger ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    options.addOption("radius", new Float(1));
	    System.out.println("Creating Processor");
	    PeakMerger peakMerger = new PeakMerger();
	    System.out.println("Running Processor");
	    myCase = peakMerger.process(myCase, options);
	    System.out.println("Done!\n");
	}
	if (peakRemoverFlag != 0) {
	    System.out.println("### Applying PeakRemover ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    options.addOption("sizeThreshold", new Float(10));
	    options.addOption("intensityThreshold", new Float(10));
	    System.out.println("Creating Processor");
	    PeakRemover peakRemover = new PeakRemover();
	    System.out.println("Running Processor");
	    myCase = peakRemover.process(myCase, options);
	    System.out.println("Done!\n");
	}


	System.out.println("FINAL RESULT\n\n");
	System.out.println("Saving the images.");
	saveImageStack(baseName, myCase.getStack());

    }

    private Stack getImageStack(String baseName) throws Exception {
	Stack stack = new Stack();
	String fname = "";
	for (int i = 0; i < 20; ++i) {
	    if (i < 10) {
		fname = baseName + "0" + i + ".jpg";
	    } else {
		fname = baseName + i + ".jpg";
	    }
	    System.out.println("Opening image: " + fname);
	    Image image = getImage(fname);
	    stack.addImage(image);
	}
	return stack;
    }

    private Image getImage(String baseName) {
	BufferedImage bi = null;
	try {
	    bi = ImageIO.read(new File(baseName));
	} catch (IOException e) {
	    System.out.println("Couldn't read file " + baseName + ": " + e.getMessage());
	}
	int w = bi.getWidth(null);
	int h = bi.getHeight(null);
	Image image = new Image(w, h);
	for (int i = 0; i < w; i++) {
	    for (int j = 0; j < h; j++) {
		image.setIntensity(i, j, bi.getRaster().getSampleFloat(i, j, 0));
	    //System.out.println("Setting intensity: "+image.getIntensity(i,j));
	    }
	}
	return image;
    }

    private float[] handlepixels(BufferedImage img, int x, int y, int w, int h) {
	int[] pixels = new int[w * h];
	float[] floatPixels = new float[w * h];
	PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
	try {
	    pg.grabPixels();
	} catch (InterruptedException e) {
	    System.err.println("interrupted waiting for pixels!");
	    return null;
	}
	if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
	    System.err.println("image fetch aborted or errored");
	    return null;
	}
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		floatPixels[j * w + i] = handlesinglepixel(pixels[j * w + i]);
	    }
	}
	return floatPixels;
    }

    public float handlesinglepixel(int pixel) {
	int alpha = (pixel >> 24) & 0xff;
	int red = (pixel >> 16) & 0xff;
	int green = (pixel >> 8) & 0xff;
	int blue = (pixel) & 0xff;

	return (float) (red);
    }

    private void saveImageStack(String baseName, Stack stack) {
	for (int i = 0; i < stack.getDepth(); i++) {
	    Image image = stack.getImage(i);
	    String fname = "";
	    if (i < 10) {
		fname = baseName + "0" + i + ".jpg";
	    } else {
		fname = baseName + i + ".jpg";
	    }
	    try {
		BufferedImage bi = image.getImage();//toAwtImage(image); // retrieve image
		ImageIO.write(bi, "jpg", new File(fname));
	    } catch (IOException e) {
	    }
	}
    }

    private BufferedImage toAwtImage(Image image) {
	BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
	for (int i = 0; i < image.getWidth(); i++) {
	    for (int j = 0; j < image.getHeight(); j++) {
		float intensity = image.getIntensity(i, j) * 255;
		bi.getRaster().setSample(i, j, 0, intensity);
	    }
	}
	return bi;
    }

    public static void main(String[] argv) {
	try {
	    new TextGUI2(argv[0]);
	} catch (Exception e) {
	    System.out.print("Error: ");
	    System.out.println(e.getMessage());
	}
    }
}
