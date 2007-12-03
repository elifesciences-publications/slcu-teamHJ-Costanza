package costanza;

/**
 * Invert a stack by setting all intensity values to
 * stack.maxIntensityLimit()-stack.Intensity(xI,yI,zI).
 *
 * @see         Processor
 */
public class Inverter extends Processor {
    
    public Case process(Case c, Options options) throws Exception {
        Stack stack = c.getStack();
        if(stack == null){
            throw new Exception("No working stack initialised in case");
        }
        float max = stack.getMaxIntensityLimit();
        int depth = stack.getDepth();
        int height = stack.getHeight();
        int width = stack.getWidth();
        
        for (int zI=0; zI<depth; ++zI) {
            
            for (int yI=0; yI<height; ++yI) {
                for (int xI=0; xI<width; ++xI) {
                    stack.setIntensity(xI,yI,zI,max-
                            stack.getIntensity(xI,yI,zI));
                }
            }
        }
        return c;
    }
}
