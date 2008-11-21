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
    public static final String INTENSITY_LEVELS_OPT = "intensityLevelsNumber";
    public static final String THRESHOLD_OPT = "threshold";
    @Override
    public Case process(Case c, Options options) throws Exception {
	//System.out.println("BackgroundFinderIntensity::process");

	// Get threshold value from options
        int INTENSITY_LEVELS = Case.COSTANZA_INTENSITY_LEVELS;
        double threshold = 0;
        if(options != null)
        {
            if(options.hasOption(INTENSITY_LEVELS_OPT))
                INTENSITY_LEVELS = ((Integer) options.getOptionValue(INTENSITY_LEVELS_OPT)).intValue();
            threshold = ((Float) options.getOptionValue(THRESHOLD_OPT)).floatValue()/(float)INTENSITY_LEVELS;
        }
        else
            return c;

	// Check that there is an working stack in case
	if (c.getStack() == null) {
	    throw new Exception("No working stack available in Case.");
	}
      
        Stack s = c.getStack();
	int width = s.getWidth();
	int height = s.getHeight();
	int depth = s.getDepth();
       // Create vector with pixels that stores the background
	//StackBackground bgPixel = new StackBackground();
        
        PixelFlag pf = (PixelFlag) c.getStackData(DataId.PIXEL_FLAG);
        if (pf == null) {
            throw new Exception("Pixel flags not set for the stack.");
        }
//        PixelFlag pf = new PixelFlag(width, height, depth);
        
        
	// Extract pixels with intensity lower than threshold into background
	for (int x = 0; x < width; ++x) {
	    for (int y = 0; y < height; ++y) {
		for (int z = 0; z < depth; ++z) {
		    if (s.getIntensity(x, y, z) < threshold) {
                        pf.setBackground(x, y, z);
		    }                      
		}
	    }
	}
        
//        c.attachStackData(pf);
	return c;
    }
}
