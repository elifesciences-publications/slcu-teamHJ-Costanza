package Costanza;

/**
 * Invert a stack by setting all intensity values to
 * stack.maxIntensityLimit()-stack.Intensity(xI,yI,zI).
 *
 * @see         Processor
 */
public class Inverter extends Processor {
    
    public Case process(Case c, Options options) throws Exception {
        System.out.println("Inside process");
        Stack stack = c.getStack();
        if(stack == null){
            throw new Exception("No working stack initialised in case");
        }
        System.out.println("Inside process 1");
        float max = stack.getMaxIntensityLimit();
        System.out.println("Inside process 2");
        int depth = stack.getDepth();
        System.out.println("Inside process3");
        int height = stack.getHeight();
        System.out.println("Inside process4");
        int width = stack.getWidth();
        System.out.println("Inside process5");
        
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
