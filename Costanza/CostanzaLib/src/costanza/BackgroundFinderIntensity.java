package costanza;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
/**
 * Collects all pixels below a threshold intensity into the background
 *
 *
 * @see Processor
 */
public class BackgroundFinderIntensity extends Processor {
    
	public Case process(Case c, Options options) throws Exception {
		//System.out.println("BackgroundFinderIntensity::process");
		
		// Get threshold value from options
		Float tmpThreshold = (Float) (options.getOptionValue("threshold"));
		float threshold = tmpThreshold.floatValue();

    
		// Check that there is an working stack in case
		if( c.getStack() == null) {
			throw new Exception("No working stack available in case.");
		}
		
		// Create vector with pixels that stores the background
		Vector<Pixel> bgPixel = new Vector<Pixel>();
		
		int width = c.getStack().getWidth();
		int height = c.getStack().getHeight();
		int depth = c.getStack().getDepth();

		// Extract pixels with intensity lower than threshold into background
		for (int x=0; x<width; ++x) {
			for (int y=0; y<height; ++y) {
				for (int z=0; z<depth; ++z) {
					if (c.getStack().getIntensity(x,y,z)<threshold) {
						bgPixel.add(new Pixel(x,y,z));
					}
				}
			}
		}
		
		// Create background BOA and add it to data structure
		BOA bgBoa = new BOA(-1,bgPixel);
		c.getData().attachData(DataId.stackBackground,bgBoa);
		
		return c;
	}
}
