
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
import costanza.Pixel;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.imageio.ImageIO;

/**
 * The Text version of our GUI.
 * @author michael
 */
public class TextGUI {

    /** Creates a new instance of TextGUI.
     * 
     * @param baseName the name to use as base for loading and saving images.
     * @throws java.lang.Exception
     */
    public TextGUI(String baseName) throws Exception {
        final boolean invertFlag = false;
        final boolean meanFilterFlag = true;
        final boolean gradientDescentFlag = false;
        final boolean intensityFinderFlag = false;
        final boolean peakMergerFlag = false;
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
            options.addOption("radius", new Float(1));
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
            options.addOption("extendedNeighborhood", new Integer(0));
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

        System.out.println("FINAL RESULT\n\n");
        System.out.println("Saving the images.");
        writeImageStack(baseName, myCase.getStack());

    }

    /** Read some images into a stack.
     * 
     * @param baseName the start of the name of the images to read.
     * @param numImages the number of images to read.
     * @param randomImages whether to generate random images or not.
     * @return a stack of images.
     * @throws java.lang.Exception
     */
    private Stack readImageStack(String baseName, int numImages, boolean randomImages) throws Exception {
        Stack stack = new Stack();
        if (randomImages) {
            for (int i = 0; i < numImages; ++i) {
                //stack.addImage(createRandomImage(100, 100));
                stack.addImage(createSinglePixelImage(100, 100));
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

    /** Read in an image.
     * 
     * @param baseName the name of the image to read.
     * @return the image in our format.
     * @throws java.io.IOException
     */
    private Image readImage(String baseName) throws IOException {
        BufferedImage bi = ImageIO.read(new File(baseName));
        Image image = new Image(bi);
        /*
        System.out.println("Read: Image type: " + bi.getType());
        System.out.println("Read: Pixel(5,5): " + image.getIntensity(5, 5));
        System.out.println("Read: Pixel(5,5): " + bi.getRGB(5, 5));
        System.out.println("Read: Pixel(5,5): " + bi.getRaster().getSampleFloat(5, 5, 0));
         */
        return image;
    }

    /** Write a stack of images to files.
     * 
     * @param baseName the base name of the files to write to.
     * @param stack the stack of images to write.
     * @throws java.io.IOException
     */
    private void writeImageStack(String baseName, Stack stack) throws IOException {
        for (int i = 0; i < stack.getDepth(); i++) {
            Image image = stack.getImage(i);
            //System.out.println("W: " + image.getWidth() + " H: " + image.getHeight());
            /*
            System.out.println("Write: Image type: " + image.getImage().getType());
            System.out.println("Write: Pixel(5,5): " + image.getIntensity(5, 5));
            System.out.println("Write: Pixel(5,5): " + image.getImage().getRGB(5, 5));
            System.out.println("Write: Pixel(5,5): " + image.getImage().getRaster().getSampleFloat(5, 5, 0));
             */
            String fname = "";
            if (i < 10) {
                fname = baseName + "0" + i + "New.jpg";
            } else {
                fname = baseName + i + "New.jpg";
            }
            boolean write = ImageIO.write(image.getImage(), "jpeg", new File(fname));
            System.out.println("Writing image " + fname + ": " + write);
        //printImage(image);
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
                image.setIntensity(i, j, (i == j) ? 1 : 0);
            }
        }
        return image;
    }

    /** Simple method for setting an initial image to a diagonal matrix.
     * @param w the width of the created image to use.
     * @param h the height of the created image to use.
     * @return a random image.
     */
    private Image createRandomImage(int w, int h) {
        Image image = new Image(w, h);
        for (int i = 0; i < image.getWidth(); ++i) {
            for (int j = 0; j < image.getHeight(); ++j) {
                float intensity = (float) Math.random();
                image.setIntensity(i, j, intensity);
            }
        }
        return image;
    }

    /** Print an Image to the terminal.
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

    private static void tryRedPixels() throws IOException {
        Vector<Pixel> reds = new Vector<Pixel>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                reds.add(new Pixel(i, j, 0));
            }
        }

        File file = new File("/home/whitman/michael/projects/imageProcessing/data/43SmallZoom/jpg/009.jpg");
        Image image = new Image(ImageIO.read(file));
        BufferedImage bi = image.markPixels(reds, reds, reds);
        boolean write = ImageIO.write(bi, "jpg", new File("apa.jpg"));
    }

    public static void main(String[] argv) {
        try {
            new TextGUI(argv.length > 0 ? argv[0] : "");
        //tryConversion();
        //tryRedPixels();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
