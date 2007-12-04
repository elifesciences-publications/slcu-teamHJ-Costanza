/*
 * PeakMerger.java
 *
 * Created on December 1, 2007, 12:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package costanza;

/**
 * PeakMerger merges peaks if they are closer than some threshold.
 *
 * @author pontus
 */
public class PeakMerger extends Processor {

    private int bigInt = 100000;

    /** Creates a new instance of PeakMerger */
    public PeakMerger() {
    }

    /**
     * Implements the PeakMerger algorithm.
     * @param a Case and an Option
     * @returns a modified Case object
     * @see Processor
     */
    public Case process(Case c, Options o) throws Exception {
	float R = ((Float) o.getOptionValue("radius")).floatValue();
	float[] scale = c.getStack().getScale();
	Object obj[] = c.getData().getData(DataId.cellCenters).toArray();
	int numObj = obj.length;
	int i = 0;
	for (; i < numObj; ++i) {
	    Object o1 = obj[i];
	    for (Object o2 : c.getData().getData(DataId.cellCenters).toArray()) {
		boolean merged =
			testForMerging(o1, o2, scale, R, c.getManipulator());
		if (merged) {
		    i=0;
		    obj = c.getData().getData(DataId.cellCenters).toArray();
		    numObj = obj.length;
		    break;
		}
	    }
	}
	return c;

    }

    private boolean testForMerging(Object o1, Object o2, float[] scale,
	    float R, CellDataManipulator manip) {
	boolean merged = false;
	if (!o1.toString().equals(o2.toString())) {
	    CellCenter cc1 = (CellCenter) o1;
	    CellCenter cc2 = (CellCenter) o2;
	    if (getDistance(cc1, cc2, scale) < R) {
		//System.out.println("merge " + cc1.getId() + " " + cc2.getId());
		manip.mergeAllData(cc1.getId(), cc2.getId(), bigInt++);
		//System.out.println("Done");
		merged = true;
	    }
	}
	return merged;
    }

    /**
     * Calculates the distance between two CellCenters taking the
     * scale int account.
     * @param two CellCenter and a float []
     * @return the distance between the CellCenters
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
