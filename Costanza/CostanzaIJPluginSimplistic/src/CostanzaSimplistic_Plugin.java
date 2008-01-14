
import costanza.Case;
import costanza.Image;
import costanza.BackgroundFinderIntensity;
import costanza.MeanFilter;
import costanza.GradientDescent;
import costanza.PeakMerger;
import costanza.PeakRemover;
import costanza.IntensityFinder;
import costanza.Options;
import costanza.Stack;
import costanza.Pixel;
import costanza.CellCenter;
import costanza.DataId;
import costanza.BOA;
import costanza.StackBackground;
import costanza.CellCenterMarker;
import costanza.BoaColorizer;
import costanza.BoaColorizerIntensity;

import costanza.MeanFilter2;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageConverter;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;

import ij.*;
import ij.gui.*;
import ij.io.ImportDialog;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.process.*;
import java.util.Hashtable;
import java.util.Set;

public class CostanzaSimplistic_Plugin implements PlugInFilter {

    private ImagePlus imagePlus;

    public int setup(String arg, ImagePlus imagePlus) {
        this.imagePlus = imagePlus;
        return DOES_ALL;
    }

    public void run(ImageProcessor imageProcessor) {
        try {

            // Generate user dialog
            //
            GenericDialog gd = new GenericDialog("Parameter settings");

            gd.addMessage("Background extraction:");
            gd.addCheckbox("Apply background extractor", false);
            gd.addNumericField("Background intensity threshold:", 0.1, 1);
            gd.addCheckbox("Show background stack", false);

            gd.addMessage("\nPreprocessing:");
            gd.addNumericField("Number of meanFilter runs:", 2, 0);
            gd.addNumericField("MeanFilter radius:", 2, 1);
            gd.addCheckbox("Show preprocessed stack", false);

            gd.addMessage("\nGradient Descent:");
            gd.addCheckbox("Show gradient descent centers", false);
            gd.addCheckbox("Show gradient descent boas", false);

            gd.addMessage("Postprocessing:");
            gd.addCheckbox("Apply remover:", false);
            gd.addNumericField("Minimal peak intensity:", 0, 1);
            gd.addNumericField("Minimal boa size:", 0, 1);
            gd.addCheckbox("Apply merger:", false);
            gd.addNumericField("Merging radius:", 0, 1);

            gd.addMessage("Output:");
            gd.addCheckbox("Show cell center stack:", true);
            gd.addCheckbox("Show random boa stack:", false);
            gd.addCheckbox("Show intensity boa stack:", false);

            gd.addMessage("Processing options:");
            gd.addCheckbox("Load additional intensity data:", true);

            gd.showDialog();

            if (gd.wasCanceled()) {
                IJ.error("Parameter settings canceled");
                return;
            }
            // Extract user provided values
            //bg
            boolean bgFlag = (boolean) gd.getNextBoolean();
            float bgThreshold = (float) gd.getNextNumber();
            boolean bgOutput = (boolean) gd.getNextBoolean();
            //preproc
            int smoothNum = (int) gd.getNextNumber();
            float smoothR = (float) gd.getNextNumber();
            boolean smoothOutput = (boolean) gd.getNextBoolean();
            //gd
            boolean gdCenterOutput = (boolean) gd.getNextBoolean();
            boolean gdBoaOutput = (boolean) gd.getNextBoolean();
            //postproc
            boolean removeFlag = (boolean) gd.getNextBoolean();
            float minIntensity = (float) gd.getNextNumber();
            float minSize = (float) gd.getNextNumber();
            boolean mergerFlag = (boolean) gd.getNextBoolean();
            float mergerR = (float) gd.getNextNumber();
            //output
            boolean ccOutFlag = (boolean) gd.getNextBoolean();
            boolean boaOutFlag = (boolean) gd.getNextBoolean();
            boolean boaIntensityOutFlag = (boolean) gd.getNextBoolean();

            boolean addIntensityData = (boolean) gd.getNextBoolean();

            Stack stack = createStackFromImagePlus(imagePlus);
            Case IJCase = new Case(stack);

            IJ.showMessage("Costanza", "Stack initialization finished!");

            // Set the options
            Options backgroundOptions = new Options();
            backgroundOptions.addOption("threshold", new Float(bgThreshold));
            Options meanFilterOptions = new Options();
            meanFilterOptions.addOption("radius", new Float(smoothR));
            Options gradientDescentOptions = new Options();
            Options peakRemoverOptions = new Options();
            peakRemoverOptions.addOption("intensityThreshold", new Float(minIntensity));
            peakRemoverOptions.addOption("sizeThreshold", new Float(minSize));
            Options peakMergerOptions = new Options();
            peakMergerOptions.addOption("radius", new Float(mergerR));

            // BACKGROUND EXTRACTION
            if (bgFlag) {
                BackgroundFinderIntensity backgroundFinder = new BackgroundFinderIntensity();
                try {
                    backgroundFinder.process(IJCase, backgroundOptions);
                } catch (Exception ex) {
                    error("Error in backgroundfinder: " + ex.getMessage() + "\n");
                }
                Collection<Pixel> bgCollection = (StackBackground) IJCase.getStackData(DataId.BACKGROUND);
                IJ.showMessage("Costanza", "Backgroundfinder with intensity threshold " + bgThreshold + " found " + bgCollection.size() + " bg pixels.");
                if (bgOutput) {
                    showBackground(IJCase.getOriginalStack(), bgCollection, "Background");
                }
            }

            // MEAN FILTER
            if (smoothNum > 0) {
                //MeanFilter meanFilter = new MeanFilter();
                MeanFilter2 meanFilter = new MeanFilter2();
                for (int i = 0; i < smoothNum; ++i) {
                    try {
                        meanFilter.process(IJCase, meanFilterOptions);
                    } catch (Exception ex) {
                        error("Error in meanfilter: " + ex.getMessage() + "\n");
                    }
                }
                if (smoothOutput) {
                    ImagePlus tmp = createImagePlusFromStack(IJCase.getStack());
                    tmp.show();
                }
                IJ.showMessage("Costanza", "MeanFilter with radius " + smoothR + " applied " + smoothNum + " times.");
            }

            // GRADIENT DESCENT
            GradientDescent gradientDescent = new GradientDescent();
            try {
                gradientDescent.process(IJCase, gradientDescentOptions);
            } catch (Exception ex) {
                error("Error in GradientDescent: " + ex.getMessage() + "\n");
            }
            if (gdCenterOutput) {
                // Run process for generating red pixels in current cell centers
                Options tmpOptions = new Options();
                CellCenterMarker cellCenterMarker = new CellCenterMarker();
                try {
                    cellCenterMarker.process(IJCase, tmpOptions);
                } catch (Exception ex) {
                    error("Error in cellCenterMarker: " + ex.getMessage() + "\n");
                }
                // Plot the result
                ImagePlus ipTmp = createImagePlusFromResultStack(IJCase, "Cell centers after gd");
                ipTmp.show();
            }
            if (gdBoaOutput) {
                // Process for generating random colored boas
                Options tmpOptions = new Options();
                BoaColorizer boaColorizer = new BoaColorizer();
                try {
                    boaColorizer.process(IJCase, tmpOptions);
                } catch (Exception ex) {
                    error("Error in boaColorizer: " + ex.getMessage() + "\n");
                }
                // Plot boas
                ImagePlus ipTmp = createImagePlusFromResultStack(IJCase, "BOAs after gd");
                ipTmp.show();
            }
            int numPeak = IJCase.sizeOfData(DataId.CENTERS);
            IJ.showMessage("Costanza", "GradientDescent found " + numPeak + " peaks.");

            // PEAK REMOVER
            if (removeFlag) {
                PeakRemover peakRemover = new PeakRemover();
                try {
                    peakRemover.process(IJCase, peakRemoverOptions);
                } catch (Exception ex) {
                    error("Error in PeakRemover: " + ex.getMessage() + "\n");
                }
                numPeak = IJCase.sizeOfData(DataId.CENTERS);
                IJ.showMessage("Costanza", "PeakRemover removed into " + numPeak + " peaks.");
            }

            // PEAK MERGER
            if (mergerFlag) {
                PeakMerger peakMerger = new PeakMerger();
                try {
                    peakMerger.process(IJCase, peakMergerOptions);
                } catch (Exception ex) {
                    error("Error in PeakMerger: " + ex.getMessage() + "\n");
                }
                numPeak = IJCase.sizeOfData(DataId.CENTERS);
                IJ.showMessage("Costanza", "PeakMerger merged into " + numPeak + " peaks.");
            }

            // FINAL OUTPUT
            if (ccOutFlag) {
                // Run process for generating red pixels in current cell centers
                Options tmpOptions = new Options();
                CellCenterMarker cellCenterMarker = new CellCenterMarker();
                try {
                    cellCenterMarker.process(IJCase, tmpOptions);
                } catch (Exception ex) {
                    error("Error in cellCenterMarker: " + ex.getMessage() + "\n");
                }
                // Plot the result
                ImagePlus ipTmp = createImagePlusFromResultStack(IJCase, "Cell centers");
                ipTmp.show();
            }
            if (boaOutFlag) {
                // Process for generating random colored boas
                Options tmpOptions = new Options();
                BoaColorizer boaColorizer = new BoaColorizer();
                try {
                    boaColorizer.process(IJCase, tmpOptions);
                } catch (Exception ex) {
                    error("Error in boaColorizer: " + ex.getMessage() + "\n");
                }
                // Plot boas
                ImagePlus ipTmp = createImagePlusFromResultStack(IJCase, "BOAs");
                ipTmp.show();
            }
            if (boaIntensityOutFlag) {
                // Process for generating random colored boas
                Options tmpOptions = new Options();
                BoaColorizerIntensity boaColorizer = new BoaColorizerIntensity();
                try {
                    boaColorizer.process(IJCase, tmpOptions);
                } catch (Exception ex) {
                    error("Error in boaColorizerIntensity: " + ex.getMessage() + "\n");
                }
                // Plot boas
                ImagePlus ipTmp = createImagePlusFromResultStack(IJCase, "Intensity in BOAs");
                ipTmp.show();
            }
            //Read additional intensity data
            if (addIntensityData) {
                boolean cont = true;
                int counter = 0;
                while (cont) {
                    GenericDialog ngd = new GenericDialog("Processing option");
                    ngd.addMessage("Read new intensity data?");
                    ngd.showDialog();
                    if (ngd.wasOKed()) {
                        
                        ij.IJ.run("Image Sequence...");
                        ImagePlus imp = ij.IJ.getImage();
                        int sz = imp.getImageStackSize();
                        String imName = imp.getTitle();
                        
                        Stack intensStack = createStackFromImagePlus(imp);
                       
                        ++counter;
                        String stackTag = "UserStack" + counter;
                        Options op = new Options();
                        op.addOption("OverrideStack", intensStack);
                        op.addOption("StackTag", stackTag);
                                
                        IntensityFinder intFinder = new IntensityFinder();
                        intFinder.process(IJCase, op);
                                
                        System.out.println(sz + ", " + imName);
                    }
                    else{
                        cont = false;
                    }
                }
            }
        } catch (Exception exception) {
            error(exception.getMessage());
        }
    }

    /** Create new Stack object from ij.ImagePlus object (taken from utility). */
    static public Stack createStackFromImagePlus(ij.ImagePlus imagePlus) throws Exception {
        ij.ImageStack imageStack = imagePlus.getStack();
        int slices = imageStack.getSize();

        Stack stack = new Stack();
        for (int n = 1; n <= slices; ++n) {
            ij.process.ImageProcessor sliceProcessor = imageStack.getProcessor(n);
            Image image = new Image(sliceProcessor.createImage());
            stack.addImage(image);
        }

        ij.measure.Calibration calibration = imagePlus.getCalibration();
        stack.setXScale((float) calibration.pixelWidth);
        stack.setYScale((float) calibration.pixelHeight);
        stack.setZScale((float) calibration.pixelDepth);

        return stack;
    }

    /** Creates new ij.ImagePlus object from Stack (taken from utility). */
    static public ij.ImagePlus createImagePlusFromStack(Stack stack) throws Exception {
        int width = stack.getWidth();
        int height = stack.getHeight();

        ij.ImageStack imageStack = new ij.ImageStack(width, height);

        for (int i = 0; i < stack.getDepth(); ++i) {
            Image image = stack.getImage(i);
            imageStack.addSlice("", getImageProcessorFromImage(image));
        }

        ij.ImagePlus imagePlus = new ij.ImagePlus("Costanza Image", imageStack);
        ij.measure.Calibration calibration = imagePlus.getCalibration();
        calibration.pixelWidth = stack.getXScale();
        calibration.pixelHeight = stack.getYScale();
        calibration.pixelDepth = stack.getZScale();

        return imagePlus;
    }

    /** Creates new ij.ImagePlus object from Stack (taken from utility). */
    static public ij.ImagePlus createImagePlusFromResultStack(Case c, String name) throws Exception {
        int width = c.getStack().getWidth();
        int height = c.getStack().getHeight();
        int depth = c.getStack().getDepth();

        ij.ImageStack imageStack = new ij.ImageStack(width, height);

        BufferedImage[] resultImage = c.getResultImages();

        for (int i = 0; i < depth; ++i) {
            ij.ImagePlus ipTmp = new ij.ImagePlus("", resultImage[i]);
            ij.ImageStack stackTmp = ipTmp.getImageStack();
            if (stackTmp.getSize() != 1) {
                throw new Exception("Unexpected error in getImagePlusFromResultStack()");
            }
            imageStack.addSlice("", stackTmp.getProcessor(1));


        //Image image = (Image) resultImage[i];
        //	imageStack.addSlice("", getImageProcessorFromImage(image));
        }

        ij.ImagePlus imagePlus = new ij.ImagePlus(name, imageStack);
        ij.measure.Calibration calibration = imagePlus.getCalibration();
        calibration.pixelWidth = c.getStack().getXScale();
        calibration.pixelHeight = c.getStack().getYScale();
        calibration.pixelDepth = c.getStack().getZScale();

        return imagePlus;
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

    private void showPixelsInStack(Stack stack, Vector<Pixel> pixels, String name) throws Exception {
        int width = stack.getWidth();
        int height = stack.getHeight();

        ImageStack is = new ImageStack(width, height);
        for (int i = 0; i < stack.getDepth(); ++i) {
            Image image = stack.getImage(i);
            FloatProcessor fp = new FloatProcessor(image.getWidth(), image.getHeight());
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    float value = image.getIntensity(x, y);
                    fp.setf(x, y, value);
                }
            }
            is.addSlice("Img", fp);
        }
        ImagePlus ip = new ImagePlus(name, is);
        ImageConverter ic = new ImageConverter(ip);
        ic.convertToRGB();

        //Extract rgb pixels and set centers to red color;
        ImageStack is2;
        is2 = ip.getStack();
        int dimension = is2.getHeight() * is2.getWidth();
        int depth = is2.getSize();
        int[][] ipix = new int[depth][dimension];
        //IJ.showMessage("Costanza", "New stack num " + depth + ".");
        for (int z = 0; z < depth; ++z) {
            float[] tmp;
            // IJ.showMessage("Slice " + z + "before extraction");
            tmp = (float[]) is2.getPixels(z + 1);

            //IJ.showMessage("Slice " + z);
            for (int d = 0; d < dimension; ++d) {
                ipix[z][d] = (int) tmp[d];
            }
        }
        int numPixel = pixels.size();
        //IJ.showMessage("Costanza", "Found " + numPixel + " cells.");
        for (int iI = 0; iI < numPixel; ++iI) {
            ipix[pixels.get(iI).getZ()][pixels.get(iI).getX() + width * pixels.get(iI).getY()] = (int) 0xff0000;
        }
        for (int z = 0; z < depth; ++z) {
            is2.setPixels(ipix[z], z + 1);
        }
        ImagePlus ip2 = new ImagePlus(name, is2);
        ip2.show();
    }

    private void showBoasInStack(Stack stack, Vector<BOA> boas, String name) {
        int width = stack.getWidth();
        int height = stack.getHeight();

        ImageStack is = new ImageStack(width, height);
        for (int i = 0; i < stack.getDepth(); ++i) {
            Image image = stack.getImage(i);
            FloatProcessor fp = new FloatProcessor(image.getWidth(), image.getHeight());
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    float value = image.getIntensity(x, y);
                    fp.setf(x, y, value);
                }
            }
            is.addSlice("Img", fp);
        }
        ImagePlus ip = new ImagePlus(name, is);

        ip.show();
    }

    private void showBackground(Stack stack, Collection<Pixel> pixels, String name) throws Exception {
        int width = stack.getWidth();
        int height = stack.getHeight();
        int depth = stack.getDepth();

        ImagePlus imag = NewImage.createByteImage(name, width, height, depth, NewImage.FILL_WHITE);
        ImageProcessor imag_proc = imag.getProcessor();
        //ImageStack imag_stack = imag.getStack();

        Iterator<Pixel> iter = pixels.iterator();
        while (iter.hasNext()) {
            Pixel p = iter.next();
            imag.setSlice(p.getZ() + 1);
            imag_proc.putPixel(p.getX(), p.getY(), 0);
        }

        //IJ.showMessage("Costanza", "Showing background.");
        imag.show();
    }

    private void error(String message) {
        System.out.println("An error occured: " + message + "\n");
        IJ.showMessage("Error", "An error occured: " + message + "\n");
    }
}
