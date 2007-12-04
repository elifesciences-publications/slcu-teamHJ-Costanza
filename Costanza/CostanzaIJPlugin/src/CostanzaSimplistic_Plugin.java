import costanza.Case;
import costanza.Driver;
import costanza.Factory;
import costanza.Image;
import costanza.Inverter;
import costanza.BackgroundFinderIntensity;
import costanza.MeanFilter;
import costanza.GradientDescent;
import costanza.PeakMerger;
import costanza.PeakRemover;
import costanza.Job;
import costanza.Options;
import costanza.Queue;
import costanza.Stack;
import costanza.Pixel;
import costanza.CellCenter;
import costanza.DataId;
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
		//initFactory();
		this.imagePlus = imagePlus;
		return DOES_ALL;
	}
  
	public void run(ImageProcessor imageProcessor) {
		try {
			
			// Generate user dialog
			//
			GenericDialog gd = new GenericDialog("Parameter settings");

			gd.addMessage("Background extraction:");
			gd.addCheckbox("Apply background extractor:",false);
			gd.addNumericField("Background intensity threshold:",0,1);

			gd.addMessage("Preprocessing:");
			gd.addNumericField("Number of smooth runs:",0,0);
			gd.addNumericField("Smooth radius:",0,1);
			
			gd.addMessage("Postprocessing:");
			gd.addCheckbox("Apply merger:",false);
			gd.addNumericField("Merging radius:",0,1);
			gd.addCheckbox("Apply remover:",false);
			gd.addNumericField("Minimal peak intensity:",0,1);
			gd.addNumericField("Minimal boa size:",0,1);

			gd.addMessage("Output:");
			gd.addCheckbox("Show cell center stack:",true);
			gd.addCheckbox("Show boa stack:",true);

			gd.showDialog();
			
			if( gd.wasCanceled() ) {
				IJ.error("Parameter settings canceled");
				return;
			}
			
			// Extract user provided values
			boolean bgFlag = (boolean) gd.getNextBoolean();
			float bgThreshold = (float) gd.getNextNumber();
			int smoothNum = (int) gd.getNextNumber();
			float smoothR = (float) gd.getNextNumber();
			boolean mergerFlag = (boolean) gd.getNextBoolean();
			float mergerR = (float) gd.getNextNumber();
			boolean removeFlag = (boolean) gd.getNextBoolean();
			float minIntensity = (float) gd.getNextNumber();
			float minSize = (float) gd.getNextNumber();
			boolean ccOutFlag = (boolean) gd.getNextBoolean();			
			boolean boaOutFlag = (boolean) gd.getNextBoolean();			

			Stack stack = createStackFromImagePlus();
			Case IJCase = new Case(stack);
      
			IJ.showMessage("Costanza", "Stack initialization finished!");
      
			// Set the options
			Options backgroundOptions = new Options();
			backgroundOptions.addOption("threshold", new Float(smoothR));
			Options meanFilterOptions = new Options();
			meanFilterOptions.addOption("radius", new Float(smoothR));
			Options gradientDescentOptions = new Options();
			Options peakMergerOptions = new Options();
			peakMergerOptions.addOption("radius", new Float(mergerR));
			Options peakRemoverOptions = new Options();
			peakRemoverOptions.addOption("intensityThreshold", new Float(minIntensity));
			peakRemoverOptions.addOption("sizeThreshold", new Float(minSize));
      
			// BACKGROUND EXTRACTION
			if (bgFlag) {
				BackgroundFinderIntensity backgroundFinder = new BackgroundFinderIntensity();
				try {
					backgroundFinder.process(IJCase, backgroundOptions);
				} catch (Exception ex) {
					error("Error in backgroundfinder: " + ex.getMessage() + "\n");
				}
				IJ.showMessage("Costanza", "Backgroundfinder with intensity threshold "+bgThreshold+" applied.");
			}
			
			// MEAN FILTER
			if (smoothNum>0) {
				MeanFilter meanFilter = new MeanFilter();
				for (int i=0; i<smoothNum; ++i) {
					try {
						meanFilter.process(IJCase, meanFilterOptions);
					} catch (Exception ex) {
						error("Error in meanfilter: " + ex.getMessage() + "\n");
					}
				}
				IJ.showMessage("Costanza", "MeanFilter with radius "+smoothR+" applied "+smoothNum+" times.");
			}
      
			// GRADIENT DESCENT
			GradientDescent gradientDescent = new GradientDescent();
			try {
				gradientDescent.process(IJCase, gradientDescentOptions);
			} catch (Exception ex) {
				error("Error in GradientDescent: " + ex.getMessage() + "\n");
			}
			int numPeak = IJCase.getData().sizeOfData(DataId.cellCenters);
			IJ.showMessage("Costanza", "GradientDescent found "+numPeak+" peaks.");
			
			// PEAK MERGER
			if (mergerFlag) {
				PeakMerger peakMerger = new PeakMerger();
				try {
					peakMerger.process(IJCase, peakMergerOptions);
				} catch (Exception ex) {
					error("Error in PeakMerger: " + ex.getMessage() + "\n");
				}
				numPeak = IJCase.getData().sizeOfData(DataId.cellCenters);
				IJ.showMessage("Costanza", "PeakMerger merged into "+numPeak+" peaks.");
			}

			// PEAK REMOVER
			if (removeFlag) {
				PeakRemover peakRemover = new PeakRemover();
				try {
					peakRemover.process(IJCase, peakRemoverOptions);
				} catch (Exception ex) {
					error("Error in PeakRemover: " + ex.getMessage() + "\n");
				}
				numPeak = IJCase.getData().sizeOfData(DataId.cellCenters);
				IJ.showMessage("Costanza", "PeakRemover removed into "+numPeak+" peaks.");
			}
			


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
																					 0.2f+0.8f*IJCase.getOriginalStack().getIntensity(xI,yI,zI));
					}
				}
			}
			int numPixel=dataP.size();
			//IJ.showMessage("Costanza", "Found " + numPixel + " cells.");
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
						float value = image.getIntensity(x, y);
						fp.setf(x, y, value);
					}
				}
				is.addSlice("test", fp);
			}
			ImagePlus ip = new ImagePlus("Test Image", is);
			//StackConverter sc(ip);
			//sc.convertToRGB();
			
			ip.show();
		} catch (Exception exception) {
			error(exception.getMessage());
		}    
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
				float value = imageProcessor.getf(x, y);
				image.setIntensity(x, y, value);
			}
		}
		return image;
	}
  
	private void error(String message) {
		IJ.showMessage("Error", "An error occured: " + message + "\n");
	}
}


