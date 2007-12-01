/**
 * Filename     : FrameDemo_.java
 * Description  : First test to run imagej plugin
 * Author(s)    : Henrik Jonsson (henrik@thep.lu.se)
 * Created      : September 2005
 * Revision     : 
 */
import ij.*;
import ij.gui.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;

//public class FrameDemo_ extends PlugInFrame {
public class FrameDemo_ implements PlugInFilter {
    
    //public static native void findCellCenters(int h,int w,float img[]);
    
    //static {
    //System.loadLibrary("Printf1");
    //System.load("/Users/henrik/projects/jni/src/cellExtractPropertiesImage.so");
    //}

    public int setup(String arg, ImagePlus imp) {
	return DOES_ALL;
    }
    
    public void run(ImageProcessor ip) {
	
	//Convert image and get sizes and pixels into an array
	//////////////////////////////////////////////////////////////////////
	ImageProcessor ip2 = ip.convertToFloat();
	float[] pixels = (float[])ip2.getPixels();
	int h = ip2.getHeight();
	int w = ip2.getWidth();
	//Get parameters from dialog
	//////////////////////////////////////////////////////////////////////
	GenericDialog gd = new GenericDialog("Parameter settings");
	gd.addMessage("Preprocessing:");
	gd.addNumericField("Number of smooth runs:",0,0);
	gd.addNumericField("Smooth radius:",0,1);
	
	gd.addMessage("Postprocessing:");
	gd.addNumericField("Minimal intensity:",0,1);
	gd.addNumericField("Minimal size:",0,1);
	
	gd.showDialog();
	
	if( gd.wasCanceled() ) {
	    IJ.error("Parameter settings canceled");
	    return;
	}

	int smoothNum = (int) gd.getNextNumber();
	float smoothR = (float) gd.getNextNumber();
	float minIntensity = (float) gd.getNextNumber();
	float minSize = (float) gd.getNextNumber();
	//IJ.write("Test text for result window");
	IJ.write( IJ.d2s( (double)h,0) );
	IJ.write( IJ.d2s( (double)w,0) );
	IJ.write( IJ.d2s( (double)smoothNum,0) );
	IJ.write( IJ.d2s((double)smoothR,2));
	IJ.write( IJ.d2s((double)minIntensity,2));
	IJ.write( IJ.d2s((double)minSize,2));

	//Run c++ program and save data points in array
	//////////////////////////////////////////////////////////////////////
	ImagePlus img2 = new ImagePlus("Out image",ip2);
	img2.show();

    }
}
