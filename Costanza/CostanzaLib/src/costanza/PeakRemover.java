package costanza;

import java.util.Iterator;
import java.util.Vector;

/**
 * PeakRemover removes a peak if its smaller than some threshold size,
 * or if has an intensity lower than some threshold.
 *
 * @author pontus
 * @see Processor
 */
public class PeakRemover extends Processor {

    public static final String SIZE_THRESHOLD_OPT = "sizeThreshold";
    public static final String INTENSITY_TRESHOLD_OPT = "intensityThreshold";
    public static final String INTENSITY_LEVELS_OPT = "intensityLevelsNumber";
    /**
     * Implements the PeakRemover algorithm.
     * @param c the Case to work on. 
     * @param o an Option containing two thresholds for size and intensity.
     * @return a modified Case object
     * @see Processor
     */
    @Override
    public Case process(Case c, Options o) throws Exception {
        
        Stack stack = c.getStack();
        
	float sizeThreshold = 0.0f;
        int INTENSITY_LEVELS = Case.COSTANZA_INTENSITY_LEVELS;
	double intensityThreshold = 0.0;
        
        if(o != null)
        {
            sizeThreshold = ((Float) o.getOptionValue(SIZE_THRESHOLD_OPT)).floatValue();
            if(o.hasOption(INTENSITY_LEVELS_OPT))
                INTENSITY_LEVELS = ((Integer) o.getOptionValue(INTENSITY_LEVELS_OPT)).intValue();
            intensityThreshold = ((Float) o.getOptionValue(INTENSITY_TRESHOLD_OPT)).floatValue()/(float) INTENSITY_LEVELS;
        }
        else
            throw new Exception("No valid options send to PeakRemover.");
        
        //System.out.println("intensity threshold.");
        Vector<Integer> indMap = new Vector<Integer>(c.sizeOfCells());
        
	CellCenter[] centers = new CellCenter[c.sizeOfData(DataId.CENTERS)];
        c.getCellData(DataId.CENTERS, centers);
	for (int i = 0; i < centers.length; ++i) {
	    CellCenter cc = centers[i];
	    int x = cc.getX();
	    int y = cc.getY();
	    int z = cc.getZ();
	    if (stack.getIntensity(x, y, z) < intensityThreshold) {
		c.removeCell(cc.getCell());
	    }
	}
        
        //System.out.println("size threshold.");
        
        Iterator<Cell> it = c.getCells().iterator();
        while(it.hasNext())
        {
            Cell cell = it.next();
            float size = cell.size() *
		    (stack.getXScale()) *
		    (stack.getYScale()) *
		    (stack.getZScale());
            if (size < sizeThreshold) {
                it.remove();
            }
        }
        
//	BOA[] boas =  new BOA[c.sizeOfData(DataId.BOAS)];
//        c.getCellData(DataId.BOAS, boas);
//        
//	for (int i = 0; i < boas.length; ++i) {
//	    BOA boa = boas[i];//it.next();
//	    float size =
//		    boa.size() *
//		    (c.getStack().getXScale()) *
//		    (c.getStack().getYScale()) *
//		    (c.getStack().getZScale());
//	    if (size < sizeThreshold) {
//		c.removeCell(boa.getCell());
//	    //c.removeAllCellData(boa.getCell());
//	    }
//	}
//        int size1 = c.sizeOfCells();
//        Set<Integer> keys = c.getCellIds();
//        int size2 = keys.size();
//        System.out.println("Peak remover sizes :  " + size1 + "; " + size2);
//        System.out.println("Peak remover set :  " + keys);
//            Vector<Object> dat = new Vector<Object>();
//            if (cell.get(DataId.CENTERS) != null) {
//                dat.add("cent");
//            } else {
//        Iterator<Integer> iter = keys.iterator();
//        while (iter.hasNext()) {
//            Cell cell = c.getCell(iter.next());
//                dat.add(null);
//            }
//            if (cell.get(DataId.BOAS) != null) {
//                dat.add("boa ");
//            } else {
//                dat.add(null);
//            }
//            if (cell.get(DataId.INTENSITIES) != null) {
//                dat.add("inte");
//            } else {
//                dat.add(null);
//            }
//            if (cell.get(DataId.NEIGHBORS) != null) {
//                dat.add("neig");
//            } else {
//                dat.add(null);
//            }
//
//            System.out.print(cell.getCellId() + ": " + dat + "\n");
//        }
        //System.out.println("renumbering.");
        c.renumberCells(indMap);
        PixelFlag pf = (PixelFlag)c.getStackData(DataId.PIXEL_FLAG);
        pf.changeAll(indMap);
	return c;
    }
}
