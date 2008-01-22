package costanza;

/**
 * PeakRemover removes a peak if its smaller than some threshold size,
 * or if has an intensity lower than some threshold.
 *
 * @author pontus
 * @see Processor
 */
public class PeakRemover extends Processor {

    /**
     * Implements the PeakRemover algorithm.
     * @param c the Case to work on. 
     * @param o an Option containing two thresholds for size and intensity.
     * @return a modified Case object
     * @see Processor
     */
    @Override
    public Case process(Case c, Options o) throws Exception {
	float sizeThreshold = ((Float) o.getOptionValue("sizeThreshold")).floatValue();
	float intensityThreshold = ((Float) o.getOptionValue("intensityThreshold")).floatValue();

	Object[] centers = c.getCellData(DataId.CENTERS).toArray();
	for (int i = 0; i < centers.length; ++i) {
	    CellCenter cc = (CellCenter) centers[i];
	    int x = cc.getX();
	    int y = cc.getY();
	    int z = cc.getZ();
	    if (c.getStack().getIntensity(x, y, z) < intensityThreshold) {
		//c.removeAllCellData(cc.getCell());
		c.removeCell(cc.getCell());
	    }
	}
	Object[] boas = c.getCellData(DataId.BOAS).toArray();
	for (int i = 0; i < boas.length; ++i) {
	    BOA boa = (BOA) boas[i];//it.next();
	    float size =
		    boa.size() *
		    (c.getStack().getXScale()) *
		    (c.getStack().getYScale()) *
		    (c.getStack().getZScale());
	    if (size < sizeThreshold) {
		c.removeCell(boa.getCell());
	    //c.removeAllCellData(boa.getCell());
	    }
	}
//        int size1 = c.sizeOfCells();
//        Set<Integer> keys = c.getCellIds();
//        int size2 = keys.size();
//        System.out.println("Peak remover sizes :  " + size1 + "; " + size2);
//        System.out.println("Peak remover set :  " + keys);
//        Iterator<Integer> iter = keys.iterator();
//        while (iter.hasNext()) {
//            Cell cell = c.getCell(iter.next());
//            Vector<Object> dat = new Vector<Object>();
//            if (cell.get(DataId.CENTERS) != null) {
//                dat.add("cent");
//            } else {
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
	return c;
    }
}
