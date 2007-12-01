/**
 * Invert a stack by setting all intensity values to
 * stack.maxIntensityLimit()-stack.Intensity(xI,yI,zI).
 *
 * @see         Processor
 */
package Costanza;
public class Inverter extends Processor {
		
    public Case process(Case c) {
				
				float max=c.getStack().getMaxIntensityLimit();
				
				for (int zI=0; zI<c.getStack().getDepth(); ++zI) {
						for (int yI=0; yI<c.getStack().getHeight(); ++yI) {
								for (int xI=0; xI<c.getStack().getWidth(); ++xI) {
										c.getStack().setIntensity(xI,yI,zI,max-
																							c.getStack().getIntensity(xI,yI,zI));
								}
						}
				}
				return c;
    }
}
