package Costanza;

/**
 * Invert a stack by setting all intensity values to
 * stack.maxIntensityLimit()-stack.Intensity(xI,yI,zI).
 *
 * @see         Processor
 */
public class Inverter extends Processor {
    
    public Case process(Case c) {
        
        float max=c.getStack().getMaxIntensityLimit();
        
				int depth = c.getStack().getDepth();
				int height = c.getStack().getHeight();
				int width = c.getStack().getWidth();

        for (int zI=0; zI<depth; ++zI) {
            for (int yI=0; yI<height; ++yI) {
                for (int xI=0; xI<width; ++xI) {
                    c.getStack().setIntensity(xI,yI,zI,max-
																							c.getStack().getIntensity(xI,yI,zI));
                }
            }
        }
        return c;
    }
}
