package costanza;

/**
 * PeakMerger merges peaks if they are closer than some threshold.
 * @author pontus
 * @see Processor
 */
public class PeakMerger extends Processor {

    /**Implements the PeakMerger algorithm.
     * @param c the Case to work on. 
     * @param o the Option object to use.
     * @return a modified Case object
     * @see Processor
     */
    @Override
    public Case process(Case c, Options o) throws Exception {
	float R = ((Float) o.getOptionValue("radius")).floatValue();
	float[] scale = c.getStack().getScale();
	Object obj[] = c.getCellData(DataId.CENTERS).toArray();
	int numObj = obj.length;
	int i = 0;
	for (; i < numObj; ++i) {
	    Object o1 = obj[i];
	    for (Object o2 : c.getCellData(DataId.CENTERS).toArray()) {
		boolean merged =
			testForMerging(o1, o2, scale, R, c);
		if (merged) {
		    i = 0;
		    obj = c.getCellData(DataId.CENTERS).toArray();
		    numObj = obj.length;
		    break;
		}
	    }
	}

//        int size1 = c.sizeOfCells();
//        Set<Integer> keys = c.getCellIds();
//        int size2 = keys.size();
//        System.out.println("Peak merger sizes :  " + size1 + "; " + size2 );
//        System.out.println("Peak merger set :  " + keys );
//        Iterator<Integer> iter = keys.iterator();
//        while(iter.hasNext()){
//            Cell cell = c.getCell(iter.next());
//            Vector<Object> dat = new Vector<Object>();
//            if(cell.get(DataId.CENTERS) != null )
//                dat.add("cent");
//            else
//                dat.add(null);
//            if(cell.get(DataId.BOAS) != null )
//                dat.add("boa ");
//            else
//                dat.add(null);
//            if(cell.get(DataId.INTENSITIES) != null )
//                dat.add("inte");
//            else
//                dat.add(null);
//            if(cell.get(DataId.NEIGHBORS) != null )
//                dat.add("neig");
//            else
//                dat.add(null);
//
//            System.out.print( cell.getCellId() + ": " + dat + "\n");
//        }
	return c;

    }

    /**Test if two CellCenter objects should be merged.
     * @param o1 the first CellCenter.
     * @param o2 the second CellCenter.
     * @param scale the scale to use in each dimension.
     * @param R the cut off distance to use.
     * @param manip the Case to do the merging in.
     * @return true if the merging should be done, false otherwise.
     * @throws java.lang.Exception
     */
    private boolean testForMerging(Object o1, Object o2, float[] scale,
	    float R, Case manip) throws Exception {
	boolean merged = false;
	if (!o1.toString().equals(o2.toString())) {
	    CellCenter cc1 = (CellCenter) o1;
	    CellCenter cc2 = (CellCenter) o2;
	    if (getDistance(cc1, cc2, scale) < R) {
		//System.out.println("merge " + cc1.getId() + " " + cc2.getId());
		manip.mergeAllData(cc1.getCell(), cc2.getCell());
		//System.out.println("Done");
		merged = true;
	    }
	}
	return merged;
    }

    /**Calculates the distance between two CellCenters taking the
     * scale into account.
     * @param cc1 the first CellCenter.
     * @param cc2 the second CellCenter.
     * @param scale the scale to use for the different dimensions.
     * @return the distance between the two CellCenters.
     */
    private float getDistance(CellCenter cc1, CellCenter cc2, float[] scale) {
	float x1 = cc1.getX();
	float y1 = cc1.getY();
	float z1 = cc1.getZ();
	float x2 = cc2.getX();
	float y2 = cc2.getY();
	float z2 = cc2.getZ();
	//System.out.println("center 1:" + cc1.getId() + " " + x1 + " " + y1 + " " + z1);
	//System.out.println("center 2:" + cc2.getId() + " " + x2 + " " + y2 + " " + z2);
	return (float) Math.sqrt(
		(x1 - x2) * (x1 - x2) * scale[0] * scale[0] +
		(y1 - y2) * (y1 - y2) * scale[1] * scale[1] +
		(z1 - z2) * (z1 - z2) * scale[2] * scale[2]);
    }
}
