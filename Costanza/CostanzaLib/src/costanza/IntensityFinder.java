package costanza;

import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class can be used to extract intensities in individual basin of attractions.
 * @see Processor
 */
public class IntensityFinder extends Processor {

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
	Stack stack;

	if (options != null && options.hasOption("OverrideStack")) {
	    stack = (Stack) options.getOptionValue("OverrideStack");
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
                    short flag = pf.get_flag(ix, iy, iz);
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


//	// Get the basin of attractors from data in case
//	Vector<BOA> boa = new Vector<BOA>();
//	Collection boaCollection = c.getCellData(DataId.BOAS);
//	if (boaCollection != null && boaCollection.iterator().hasNext()) {
//	    //int count=0;
//	    Iterator i = boaCollection.iterator();
//	    while (i.hasNext()) {
//		BOA boaTmp = (BOA) i.next();
//		boa.add(boaTmp);
//	    //++count;
//	    }
//	//System.out.println("Number of collected BOAs " + count);
//	} else {//No boas found, nothing to do for this function
//	    return c;
//	}
//	// Extract and save the intensities
//	int numBoa = boa.size();
//	Vector<Float> intensity = new Vector<Float>();
//	Vector<Float> meanIntensity = new Vector<Float>();
//	for (int i = 0; i < numBoa; ++i) {
//	    intensity.add(0.0f);
//	    meanIntensity.add(0.0f);
//	}
//	for (int i = 0; i < numBoa; ++i) {
//	    Vector<Pixel> pixels = new Vector<Pixel>(boa.get(i));
//	    int numPixel = pixels.size();
//	    for (int j = 0; j < numPixel; ++j) {
//		int x = pixels.get(j).getX();
//		int y = pixels.get(j).getY();
//		int z = pixels.get(j).getZ();
//		intensity.set(i, intensity.get(i) +
//			stack.getIntensity(x, y, z));
//	    }
//	}
//	for (int i = 0; i < numBoa; ++i) {
//	    int numPixel = (boa.get(i)).size();
//	    meanIntensity.set(i, intensity.get(i) / numPixel);
//	}
        // Add the total and mean intensity
        //Vector<CellIntensity> ciTmp = new Vector<CellIntensity>();
        String meanTag = stackTag + "mean";
        for (int i = 0; i < intensities.size(); ++i) {
            float intVal = intensities.get(i);
            if (intVal > 0.0) {
                Cell cell = c.getCell(i);
                int cellSize = cell.size();
                if(cellSize == 0)
                    throw new Exception("Cell of size 0 pixels detected.");
                CellIntensity intens = (CellIntensity) cell.get(DataId.INTENSITIES);

                if (intens == null) {
                    intens = new CellIntensity(c);
                    c.attachCellData(intens, cell);
                }

                //intens.addIntensity(stackTag + "total", intensity.get(i));
                intens.addIntensity(meanTag, intVal/cellSize);
            }
        }
        System.out.println("IntensityCounter:" + c.sizeOfData(DataId.INTENSITIES));
        //System.out.println(c.getIntensityTagSet());

	return c;
    }
}
