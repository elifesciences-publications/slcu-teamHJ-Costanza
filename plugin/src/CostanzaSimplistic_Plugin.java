import Costanza.Case;
import Costanza.Driver;
import Costanza.Factory;
import Costanza.Image;
import Costanza.Inverter;
import Costanza.MeanFilter;
import Costanza.GradientDescent;
import Costanza.PeakMerger;
import Costanza.Job;
import Costanza.Options;
import Costanza.Queue;
import Costanza.Stack;
import Costanza.Pixel;
import Costanza.CellCenter;
import Costanza.DataId;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;

import ij.*;
import ij.gui.*;
import ij.process.*;

public class CostanzaSimplistic_Plugin implements PlugInFilter {
    private Factory factory;
    private ImagePlus imagePlus;
    
    public int setup(String arg, ImagePlus imagePlus) {
        initFactory();
        this.imagePlus = imagePlus;
        return DOES_ALL;
    }
    
    public void run(ImageProcessor imageProcessor) {
        try {
						
						//Get parameters from dialog
						//
						GenericDialog gd = new GenericDialog("Parameter settings");
						gd.addMessage("Preprocessing:");
						gd.addNumericField("Number of smooth runs:",0,0);
						gd.addNumericField("Smooth radius:",0,1);
						
						gd.addMessage("Postprocessing:");
						gd.addNumericField("Merging radius:",0,1);
						gd.addNumericField("Minimal intensity:",0,1);
						gd.addNumericField("Minimal size:",0,1);
						
						gd.showDialog();
						
						if( gd.wasCanceled() ) {
								IJ.error("Parameter settings canceled");
								return;
						}

						int smoothNum = (int) gd.getNextNumber();
						float smoothR = (float) gd.getNextNumber();
						float mergerR = (float) gd.getNextNumber();
						float minIntensity = (float) gd.getNextNumber();
						float minSize = (float) gd.getNextNumber();
						


            Stack stack = createStackFromImagePlus();
            Case IJCase = new Case(stack);
            
            IJ.showMessage("Costanza", "Step one finished!");
            
						// Set the options
            Options meanFilterOptions = new Options();
            meanFilterOptions.addOption("radius", new Float(smoothR));
						Options gradientDescentOptions = new Options();
            Options peakMergerOptions = new Options();
            peakMergerOptions.addOption("radius", new Float(mergerR));
            Options peakRemoverOptions = new Options();
            peakRemoverOptions.addOption("intensityThreshold", new Float(minIntensity));
            peakRemoverOptions.addOption("sizeThreshold", new Float(minSize));
            
            //Inverter inverter = new Inverter();
						// MEAN FILTER
            MeanFilter meanFilter = new MeanFilter();
						for (int i=0; i<smoothNum; ++i) {
								try {
										meanFilter.process(IJCase, meanFilterOptions);
								} catch (Exception ex) {
										error("Error in meanfilter: " + ex.getMessage() + "\n");
								}
            }
            IJ.showMessage("Costanza", "MeanFilter finished!");
            
						// GRADIENT DESCENT
						GradientDescent gradientDescent = new GradientDescent();
						try {
								gradientDescent.process(IJCase, gradientDescentOptions);
						} catch (Exception ex) {
								error("Error in GradientDescent: " + ex.getMessage() + "\n");
						}
            IJ.showMessage("Costanza", "GradientDescent finished!");
						
						// PEAK MERGER
// 						PeakMerger peakMerger = new PeakMerger();
// 						try {
// 								peakMerger.process(IJCase, peakMergerOptions);
// 						} catch (Exception ex) {
// 								error("Error in PeakMerger: " + ex.getMessage() + "\n");
// 						}
//             IJ.showMessage("Costanza", "PeakMerger finished!");
						
						// GET DATA FROM CELL CENTERS AND SET THOSE VALUES
						
						Collection dataC = IJCase.getData().getData(DataId.cellCenters);
						Vector<Pixel> dataP = new Vector<Pixel>();
						Iterator dataI = dataC.iterator();
						while (dataI.hasNext()) {
								CellCenter center = (CellCenter) dataI.next();
								dataP.add(center.getPixel());
						}

						int widthI = IJCase.getStack().getWidth();
						int heightI = IJCase.getStack().getHeight();
						int depthI = IJCase.getStack().getDepth();
						for (int xI=0; xI<widthI; ++xI) {
								for (int yI=0; yI<heightI; ++yI) {
										for (int zI=0; zI<depthI; ++zI) {
												IJCase.getStack().setIntensity(xI,yI,zI,
																											 0.5f*(1.0f+IJCase.getStack().getIntensity(xI,yI,zI)));
										}
								}
						}
						int numPixel=dataP.size();
            IJ.showMessage("Costanza", "Found " + numPixel + " cells.");
						for (int iI=0; iI<numPixel; ++iI) {
								IJCase.getStack().setIntensity(dataP.get(iI).getX(),dataP.get(iI).getY(),dataP.get(iI).getZ(),0.0f);
						}


						// DISPLAY RESULT
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
        } catch (Exception exception) {
            error(exception.getMessage());
        }
        
    }
    
    private void initFactory() {
        factory = new Factory();
        factory.register("invert", Costanza.Inverter.class);
        factory.register("meanfilter", Costanza.MeanFilter.class);
    }
    
    private Stack createStackFromImagePlus() throws Exception {
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
    
    private Image getFloatImageFromImageProcessor(ImageProcessor imageProcessor) {
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
    
    private void error(String message) {
        IJ.showMessage("Error", "An error occured: " + message + "\n");
    }
}


