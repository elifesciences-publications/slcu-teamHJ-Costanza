package costanza;

/**Invert a stack by setting all intensity values to the value you get 
 * by subtracting the current intensity from the the maximum intensity limit.
 * @see Processor
 */
public class Inverter extends Processor {
    
    /**Inverts the Stack in the Case given.
     * No Options are used in this Processor. You may safely set it to null.
     * @param c the Case holding the Stack to invert.
     * @param options not used here.
     * @return the processed Case containing the inverted Stack.
     * @throws java.lang.Exception
     */
    @Override
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
