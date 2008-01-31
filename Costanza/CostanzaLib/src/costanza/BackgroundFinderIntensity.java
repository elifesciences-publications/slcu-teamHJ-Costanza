package costanza;

/**Collects all pixels below a threshold intensity into the background.
 *
 * @see Processor
 */
public class BackgroundFinderIntensity extends Processor {

    /**Collects all pixels below a threshold intensity and put them in the 
     * background.
     * @param c the Case to work on.
     * @param options the Option object containing the "threshold" to use.
     * @return the processed Case.
     * @throws java.lang.Exception
     */
    @Override
    public Case process(Case c, Options options) throws Exception {
	//System.out.println("BackgroundFinderIntensity::process");

	// Get threshold value from options
	Float tmpThreshold = (Float) (options.getOptionValue("threshold"));
	float threshold = tmpThreshold.floatValue();

	// Check that there is an working stack in case
	if (c.getStack() == null) {
	    throw new Exception("No working stack available in case.");
	}

	// Create vector with pixels that stores the background
	StackBackground bgPixel = new StackBackground();
        
	int width = c.getStack().getWidth();
	int height = c.getStack().getHeight();
	int depth = c.getStack().getDepth();
        bgPixel.ensureCapacity(width*height*depth);
        
	// Extract pixels with intensity lower than threshold into background
	for (int x = 0; x < width; ++x) {
	    for (int y = 0; y < height; ++y) {
		for (int z = 0; z < depth; ++z) {
		    if (c.getStack().getIntensity(x, y, z) < threshold) {
			bgPixel.add(new Pixel(x, y, z));
		    }
		}
	    }
	}
        
        bgPixel.trimToSize();

	c.attachStackData(bgPixel);

	return c;
    }
}
