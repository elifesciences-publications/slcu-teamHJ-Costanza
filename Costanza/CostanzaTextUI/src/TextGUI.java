
import costanza.Image;
import costanza.Stack;
import costanza.Inverter;
import costanza.Case;
import costanza.DataId;
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
public class TextGUI {

    /** Creates a new instance of TextGUI */
    public TextGUI(String baseName) throws Exception {
	final boolean invertFlag = true;
	final boolean meanFilterFlag = true;
	final boolean gradientDescentFlag = true;
	final boolean intensityFinderFlag = true;
	final boolean peakMergerFlag = true;
	final boolean peakRemoverFlag = false;
	final boolean randomImages = (baseName.length() > 0) ? false : true;

	System.out.println("Creating a Stack");
	Stack stack = readImageStack(baseName, 20, randomImages);
	Case myCase = new Case(stack);

	if (invertFlag) {
	    System.out.println("### Applying Invert ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    System.out.println("Creating Processor");
	    Inverter inverter = new Inverter();
	    System.out.println("Running Processor");
	    myCase = inverter.process(myCase, options);
	    System.out.println("Done!\n");
	}
	if (meanFilterFlag) {
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
	if (gradientDescentFlag) {
	    System.out.println("### Applying GradientDescent ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    System.out.println("Creating Processor");
	    GradientDescent gradientDescent = new GradientDescent();
	    System.out.println("Running Processor");
	    myCase = gradientDescent.process(myCase, options);
	    System.out.println("CellCenters: " + myCase.sizeOfData(DataId.cellCenters));
	    System.out.println("BOAs: " + myCase.sizeOfData(DataId.cellBasinsOfAttraction));
	    System.out.println("Done!\n");
	}
	if (intensityFinderFlag) {
	    System.out.println("### Applying IntensityFinder ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    System.out.println("Creating Processor");
	    IntensityFinder intensityFinder = new IntensityFinder();
	    System.out.println("Running Processor");
	    myCase = intensityFinder.process(myCase, options);
	    System.out.println("CellCenters: " + myCase.sizeOfData(DataId.cellCenters));
	    System.out.println("BOAs: " + myCase.sizeOfData(DataId.cellBasinsOfAttraction));
	    System.out.println("Done!\n");
	}
	if (peakMergerFlag) {
	    System.out.println("### Applying PeakMerger ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    options.addOption("radius", new Float(1));
	    System.out.println("Creating Processor");
	    PeakMerger peakMerger = new PeakMerger();
	    System.out.println("Running Processor");
	    myCase = peakMerger.process(myCase, options);
	    System.out.println("CellCenters: " + myCase.sizeOfData(DataId.cellCenters));
	    System.out.println("BOAs: " + myCase.sizeOfData(DataId.cellBasinsOfAttraction));
	    System.out.println("Done!\n");
	}
	if (peakRemoverFlag) {
	    System.out.println("### Applying PeakRemover ###");
	    System.out.println("Creating options");
	    Options options = new Options();
	    options.addOption("sizeThreshold", new Float(10));
	    options.addOption("intensityThreshold", new Float(10));
	    System.out.println("Creating Processor");
	    PeakRemover peakRemover = new PeakRemover();
	    System.out.println("Running Processor");
	    myCase = peakRemover.process(myCase, options);
	    System.out.println("CellCenters: " + myCase.sizeOfData(DataId.cellCenters));
	    System.out.println("BOAs: " + myCase.sizeOfData(DataId.cellBasinsOfAttraction));
	    System.out.println("Done!\n");
	}


	System.out.println("FINAL RESULT\n\n");
	System.out.println("Saving the images.");
	writeImageStack(baseName, myCase.getStack());

    }

    private Stack readImageStack(String baseName, int numImages, boolean randomImages) throws Exception {
	Stack stack = new Stack();
	if (randomImages) {
	    for (int i = 0; i < numImages; ++i) {
		stack.addImage(createRandomImage(10, 10));
	    }
	} else {
	    String fname = "";
	    for (int i = 0; i < numImages; ++i) {
		if (i < 10) {
		    fname = baseName + "0" + i + ".jpg";
		} else {
		    fname = baseName + i + ".jpg";
		}
		System.out.println("Opening image: " + fname);
		Image image = readImage(fname);
		stack.addImage(image);
	    }
	}
	return stack;
    }

    private Image readImage(String baseName) throws IOException {
	BufferedImage bi = null;
	bi = ImageIO.read(new File(baseName));
	Image image = new Image(bi);
	return image;
    }

    private void writeImageStack(String baseName, Stack stack) throws IOException {
	for (int i = 0; i < stack.getDepth(); i++) {
	    Image image = stack.getImage(i);
	    System.out.println("W: " + image.getWidth() + " H: " + image.getHeight());
	    System.out.println("Pixel(5,5): " + image.getIntensity(5, 5));
	    String fname = "";
	    if (i < 10) {
		fname = baseName + "0" + i + ".jpg";
	    } else {
		fname = baseName + i + ".jpg";
	    }
	    boolean write = ImageIO.write(image.getImage(), "jpg", new File(fname));
	    System.out.println("Write success: " + write);
	}
    }

    /**
     * Simple method for setting an initial image to a diagonal matrix.
     * @param w the width of the created image to use.
     * @param h the height of the created image to use.
     */
    private Image createSinglePixelImage(int w, int h) {
	Image image = new Image(w, h);
	if (w < 6 || h < 6) {
	    return image;
	}
	for (int i = 0; i < image.getWidth(); ++i) {
	    for (int j = 0; j < image.getHeight(); ++j) {
		image.setIntensity(i, j, (i == 5 && j == 5) ? 1 : 0);
	    }
	}
	return image;
    }

    /**
     * Simple method for setting an initial image to a diagonal matrix.
     * @param w the width of the created image to use.
     * @param h the height of the created image to use.
     */
    private Image createRandomImage(int w, int h) {
	Image image = new Image(w, h);
	for (int i = 0; i < image.getWidth(); ++i) {
	    for (int j = 0; j < image.getHeight(); ++j) {
		image.setIntensity(i, j, (float) Math.random() * 255);
	    }
	}
	return image;
    }

    /**
     * Print an Image to the terminal.
     * @param image the image to print
     */
    private void printImage(Image image) {
	for (int i = 0; i < image.getHeight(); ++i) {
	    for (int j = 0; j < image.getWidth(); ++j) {
		System.out.print(image.getIntensity(j, i) + " ");
	    }
	    System.out.println();
	}
    }

    private float[] handlepixels(BufferedImage img, int x, int y, int w, int h) {
	int[] pixels = new int[w * h];
	float[] floatPixels = new float[w * h];
	PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
	try {
	    pg.grabPixels();
	} catch (InterruptedException e) {
	    System.err.println("Interrupted waiting for pixels!");
	    return null;
	}
	if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
	    System.err.println("Image fetch aborted or errored");
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

    private static void tryConversion() throws IOException {
	File file = new File("/home/whitman/michael/projects/imageProcessing/data/43SmallZoom/jpg/009.jpg");
	BufferedImage bi = ImageIO.read(file);
	for (int i = 0; i < 1000; i++) {
	    Image image = new Image(bi);
	    Image imageClone = (Image) image.clone();
	    bi = imageClone.getImage();
	}
	ImageIO.write(bi, "jpg", new File("apa.jpg"));
    }

    public static void main(String[] argv) {
	try {
	    new TextGUI(argv.length > 0 ? argv[0] : "");
	//tryConversion();
	} catch (Exception e) {
	    System.err.println("Error: " + e.getMessage());
	    e.printStackTrace();
	}
    }
}
