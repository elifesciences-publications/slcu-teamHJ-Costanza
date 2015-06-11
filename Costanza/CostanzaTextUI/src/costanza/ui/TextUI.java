package costanza.ui;

import costanza.Image;
import costanza.Stack;
import costanza.Case;
import costanza.CellCenter;
import costanza.CellIntensity;
import costanza.DataId;
import costanza.Driver;
import costanza.Factory;
import costanza.Job;
import costanza.Processor;
import costanza.Queue;
import costanza.ui.Options.Multiplicity;
import costanza.ui.Options.Separator;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

/**
 * The Text version of our UI.
 * @author michael
 */
public class TextUI {

    /** Creates a new instance of TextUI.
     * 
     * @param baseName the name to use as base for loading and saving images.
     * @throws java.lang.Exception
     */
    public TextUI(Options opt) throws Exception {


        if (!opt.check()) {
            // Print usage hints
            System.out.println("Incorrect program parameters.");
            System.out.println("Expected program parameters are:");
            printUsage();
            System.exit(1);
        }
        // Normal processing
        String cfgfile = opt.getSet().getData().get(0);
        String baseName = opt.getSet().getData().get(1);
        //optional input
        Integer n = 0;
        if (opt.getSet().getData().size() > 2) {
            n = Integer.parseInt(opt.getSet().getData().get(2));
        }
        String type = "png";

//        System.out.println("config file = " + cfgfile);
//        System.out.println("stack base name = " + baseName);
//        System.out.println("number of  = " + n);

        String centersfile = "cell_centers_";
        String boafile = "boas_";
        String boaIntensityFile = "intensity_boas_";
        String boaTextFile = null;

        if (opt.getSet().isSet("cf")) {
            centersfile = opt.getSet().getOption("cf").getResultValue(0);
//            System.out.println("centers file = " + centersfile);
        }
        if (opt.getSet().isSet("bf")) {
            boafile = opt.getSet().getOption("bf").getResultValue(0);
//            System.out.println("boa file = " + boafile);
        }
        if (opt.getSet().isSet("bif")) {
            boaIntensityFile = opt.getSet().getOption("bif").getResultValue(0);
            boaTextFile = "data.txt";
//            System.out.println("boa file = " + boaIntensityFile);
        }
         if (opt.getSet().isSet("df")) {
            boaTextFile = opt.getSet().getOption("df").getResultValue(0);
//            System.out.println("output file type = " + type);
        }
        if (opt.getSet().isSet("t")) {
            type = opt.getSet().getOption("t").getResultValue(0);
//            System.out.println("output file type = " + type);
        }

        Iterator<ImageWriter> imgWrtIt = ImageIO.getImageWritersBySuffix(type);
        if (!imgWrtIt.hasNext()) {
            System.out.println("There is no compatibile ImageWriter associated with image suffix " + type + ".Please choose different image files suffix.");
            System.exit(1);
        }
//        System.out.println("Creating a Stack");

        Stack stack = readImageStack(baseName, n);
        Case myCase = new Case(stack);
        Factory<Processor> factory = initFactory();

        ConfigurationFileReader cf = new ConfigurationFileReader();
        File file = new File(cfgfile);
        if (!file.exists()) {
            System.out.println("Configuration file: " + cfgfile + "does not exist");
            System.exit(1);
        }
        Queue queue = cf.loadProperties(file);

        Driver driver = new Driver(queue, myCase, factory);
        driver.run();
//        System.out.println("FINAL RESULT\n\n");
//        System.out.println("Saving the images.");
        List<Job> outJobs = cf.getOutputJobs();
        Iterator<Job> itr = outJobs.iterator();
        while (itr.hasNext()) {
            Job j = itr.next();
            String id = j.getProcessorId();
            String outfile = "";
            if (id.equals("cellcentermarker")) {
                outfile = centersfile;
            } else if (id.equals("boacolorize")) {
                outfile = boafile;
            } else if (id.equals("boacolorizeintensity")) {
                outfile = boaIntensityFile;
            }
            Queue writeJobs = new Queue();
            writeJobs.addJob(j);
            Driver d = new Driver(writeJobs, myCase, factory);
            d.run();
            writeImages(outfile, myCase.getResultImages(), type);
//            if (id.equals("boacolorizeintensity")) {
            if (boaTextFile != null) {
                try {
                    File f = new File( boaTextFile);
                    writeBOAIntensityTable(myCase, stack, f, false);
                    
                } catch (Exception e) {//Catch exception if any
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
        if(Boolean.parseBoolean(cf.getProperty(ConfigurationFileReader.WORKING_STACK_GUI))){
            writeImageStack("working_stack_", myCase.getStack(), type);
        }
    }

    /** Read some images into a stack.
     * 
     * @param baseName the start of the name of the images to read.
     * @param numImages the number of images to read.
     * @param randomImages whether to generate random images or not.
     * @return a stack of images.
     * @throws java.lang.Exception
     */
    private Stack readImageStack(String baseName, int numImages) throws Exception {
       
        Stack stack = new Stack();
        File base = new File(baseName);
        String stackfile = base.getName().trim();
        File dir = base.getParentFile();
        if (dir == null) {
            dir = new File(".");
        }

//            System.out.println("parent name = " + dir + "|");
//            System.out.println("stack name = " + stackfile + "|");
        if(!dir.exists()){
            System.out.println("Directory " + dir + " does not exist.");
            System.exit(1);
        }
            
        StackImageFilter sif = new StackImageFilter(dir, stackfile);
        File[] files = dir.listFiles(sif);
        if (files.length == 0) {
            System.out.println("Not matching files to open the stack. Exiting...");
            System.exit(1);
        }

        Arrays.sort(files);
        int n = files.length;
        if (numImages > 0) {
            n = (files.length <= numImages) ? files.length : numImages;
        }
        for (int i = 0; i < n; ++i) {
//                System.out.println("filename name = " + files[i].getName());
//                System.out.println("extension = " + sif.getExtension(files[i]));
//                System.out.println("number = " + sif.getNumber(files[i]));
//                System.out.println("Opening image: " + files[i]);
//                System.out.println("base name: " +files[i].getPath());
            Image image = readImage(files[i].getPath());
            stack.addImage(image);
        }
        System.out.println("Opened " + n + " \"" + baseName + "\" images.");
        return stack;
    }

    /** Read in an image.
     * 
     * @param baseName the name of the image to read.
     * @return the image in our format.
     * @throws java.io.IOException
     */
    private Image readImage(String baseName) throws IOException {
        ImageIO.scanForPlugins();
        BufferedImage bi = ImageIO.read(new File(baseName));
        if(bi == null)
        {
            System.out.println("No registered reader for requested image type. " + baseName);
            System.out.println("Registered reader types are :");
            String[] strarr = ImageIO.getWriterFileSuffixes();
                for (int i = 0; i < strarr.length; ++i) {
            System.out.println(strarr[i]);
        }
            System.exit(1);
        }
        Image image = new Image(bi);
        return image;
    }

    /** Write a stack of images to files.
     * 
     * @param baseName the base name of the files to write to.
     * @param stack the stack of images to write.
     * @throws java.io.IOException
     */
    private void writeImageStack(String baseName, Stack stack, String type) throws IOException {
        int ndig = String.valueOf(stack.getDepth()).length();
        for (int i = 0; i < stack.getDepth(); i++) {
            Image image = stack.getImage(i);
            String fname = baseName + String.format("%0" + ndig + "d", i) + "." + type;

            boolean write = ImageIO.write(image.getImage(), type, new File(fname));
//            System.out.println("Writing image " + fname + ": " + write);
        }
        System.out.println("Wrote " + stack.getDepth() + " \"" + baseName + "\" images.");
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

    public static void main(String[] argv) {
        try {
            Options opt = new Options(argv, 2, 3);
            opt.getSet().addOption("cf", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
            opt.getSet().addOption("bf", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
            opt.getSet().addOption("bif", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
            opt.getSet().addOption("df", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
            opt.getSet().addOption("t", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);

            new costanza.ui.TextUI(opt);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Factory<Processor> initFactory() {
        Factory<Processor> factory = new Factory<Processor>();
        factory.register("invert", costanza.Inverter.class);
        factory.register("meanfilter", costanza.MeanFilter.class);
        factory.register("gradientdescent", costanza.GradientDescent.class);
        factory.register("peakremover", costanza.PeakRemover.class);
        factory.register("peakmerger", costanza.PeakMerger.class);
        factory.register("backgroundextractor", costanza.BackgroundFinderIntensity.class);
        factory.register("intensityfinder", costanza.IntensityFinder.class);
        factory.register("boacolorize", costanza.BoaColorizer.class);
        factory.register("boacolorizeintensity", costanza.BoaColorizerIntensity.class);
        factory.register("cellcentermarker", costanza.CellCenterMarker.class);
        return factory;
    }

    private void writeImages(String baseName, BufferedImage[] resultImages, String type) throws IOException {
        int ndig = String.valueOf(resultImages.length).length();
        for (int i = 0; i < resultImages.length; i++) {
            BufferedImage bufferedImage = resultImages[i];
            String fname = baseName + String.format("%0" + ndig + "d", i) + "." + type;
            boolean write = ImageIO.write(bufferedImage, type, new File(fname));
//            System.out.println("Writing image " + fname + ": " + write);
        }
        System.out.println("Wrote " + resultImages.length + " \"" + baseName + "\" images.");
    }

    private void printUsage() {

        System.out.println("[-cf <cenersfile>] [-bf <boafile>] [-bif <boaintensityfile>] [-df <datafile>] <cfgfile> <stackbase> [<nfiles>] ");
        System.out.println("[-cf=<cenersfile>] - optional name of the base of the files to output the marked centers of the segmented rgions.");
        System.out.println("[-bf=<boafile>] - optional name of the base of the files to output basins of attractions of segmented regions colored with random colors.");
        System.out.println("[-bif=<boaintensityfile>] - optional name of the base of the files to output basins of attractions of segmented regions colored according to intensity of the signal in the original stack.");
        System.out.println("[-df=<datafile>] - optional name of the data file to store the table of BOA positions and average intensities in the csv format.");
        System.out.println("[-t=<extension>] - optional extension of the output image files determinig their type. The default is \"png\"");
        System.out.println("<cfgfile> - name of the costanza configuration file. The format of the file is compatibile with ImageJ version of costanza configuration file. This parameter is obligatory.");
        System.out.println("<stackbase> - name of the image stack files excluding numerical suffix. This parameter is obligatory.");
        System.out.println("[<nfiles>] - optional number of the images from the stack to read. If not supplied all the images matching <stackbase> will be read.");
    }
    
    private void writeBOAIntensityTable(Case myCase, Stack stack, File f, boolean secondaryStackOption) throws Exception {
        float xScale = myCase.getStack().getXScale();
        float yScale = myCase.getStack().getYScale();
        float zScale = myCase.getStack().getZScale();
        float volumeScale = xScale * yScale * zScale;

        String tab = "\t";
        String delim = ", ";
        String newline = "\n";
        String header = ("#Cell id"+delim+"x"+delim+"y"+delim+"z"+delim+"Boa volume"+delim+"Mean cell intensity"+newline);

         FileWriter fstream = new FileWriter(f);
         BufferedWriter out = new BufferedWriter(fstream);
                    
        int id = stack.getId();
//        if (secondaryStackOption == true) {
//            id = secondaryStack.getId();
//        }
        java.util.Set<Integer> cellIds = myCase.getCellIds();
        java.util.Iterator<Integer> iterator = cellIds.iterator();
        out.write(header);
        while (iterator.hasNext()) {
            Integer i = iterator.next();
            String line = "";
            CellCenter cellCenter = (CellCenter) myCase.getCellData(DataId.CENTERS, i);
            CellIntensity cellIntensity = (CellIntensity) myCase.getCellData(DataId.INTENSITIES, i);
            //BOA cellBoa = (BOA) IJCase.getCellData(DataId.BOAS, i);
            int cellSize = myCase.getCell(i).size();

            float x = cellCenter.getX() * xScale;
            float y = cellCenter.getY() * yScale;
            float z = cellCenter.getZ() * zScale;
            float volume = cellSize * volumeScale;
            float intensity = cellIntensity.getIntensity(id + "mean") * (float) Case.COSTANZA_INTENSITY_LEVELS;
            line = i + delim + x + delim + y + delim + z + delim + volume + delim + intensity + newline;
            out.write(line);
        }
        out.close();
    }
}
