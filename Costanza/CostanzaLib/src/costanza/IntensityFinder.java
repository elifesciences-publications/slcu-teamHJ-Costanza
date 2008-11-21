package costanza;

import java.util.Vector;
import java.util.Iterator;

/**
 * This class can be used to extract intensities in individual basin of attractions.
 * @see Processor
 */
public class IntensityFinder extends Processor {
     public static final String STACK_OPT = "OverrideStack";
    /**Finds the intensities in several BOA. 
     * @param c the Case to work on.
     * @param options the Options containing an "OverrideStack" if it should be used.
     * @return the processed Case.
     * @throws java.lang.Exception
     */
    @Override
    public Case process(Case c, Options options) throws Exception {
	//System.out.println("IntensityFinder::process");

	// Check that there is an original stack in case
//        if (c.getOriginalStack() == null) {
//            throw new Exception("No original stack available in case");
//        }
	Stack stack = null;

	if (options != null && options.hasOption(STACK_OPT)) {
	    stack = (Stack) options.getOptionValue(STACK_OPT);
	//stackTag = (String)options.getOptionValue("StackTag");
	} else {
	    stack = c.getOriginalStack();
	}
	if (stack == null) {
	    throw new Exception("No stack available");
	}
	String stackTag = Integer.toString(stack.getId());

	//System.out.println("Processing stack: " + stack.getId());

        int n_cells = c.sizeOfCells();
        Vector<Float> intensities = new Vector<Float>(n_cells); 
        for(int i = 0; i < n_cells; ++i )
            intensities.add(0.0f);
        
        int xDim = stack.getWidth();
        int yDim = stack.getHeight();
        int zDim = stack.getDepth();
        
        PixelFlag pf = (PixelFlag)c.getStackData(DataId.PIXEL_FLAG);
        for (int ix = 0; ix < xDim; ++ix) {
            for (int iy = 0; iy < yDim; ++iy) {
                for (int iz = 0; iz < zDim; ++iz) {
                    int flag = pf.getFlag(ix, iy, iz);
                    if(flag != PixelFlag.BACKGROUND_FLAG){
                        int curSize = intensities.size();
                        if(flag >= curSize){
                            intensities.setSize(flag+1);
                            for(int i = curSize; i <= flag; ++i){
                                intensities.set(i, 0.0f);
                            } 
                        }
                        float pixIntens = stack.getIntensity(ix, iy, iz);
                        intensities.set(flag, intensities.get(flag) + pixIntens);
                    }
                }
            }
        }

        String meanTag = stackTag + "mean";

        Iterator<Cell> iter = c.getCells().iterator();
        while (iter.hasNext()) {
            Cell cell = iter.next();
            float intVal = intensities.get(cell.getCellId());
            int cellSize = cell.size();
            if (cellSize == 0) {
                throw new Exception("Cell of size 0 pixels detected.");
            }
            CellIntensity intens = (CellIntensity) cell.get(DataId.INTENSITIES);

            if (intens == null) {
                intens = new CellIntensity(c);
                c.attachCellData(intens, cell);
            }
            //intens.addIntensity(stackTag + "total", intensity.get(i));
            intens.addIntensity(meanTag, intVal / cellSize);
        }

        
        System.out.println("IntensityCounter:" + c.sizeOfData(DataId.INTENSITIES));
        //System.out.println(c.getIntensityTagSet());

	return c;
    }
}
