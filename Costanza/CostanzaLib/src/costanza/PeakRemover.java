/*
 * PeakRemover.java
 *
 * Created on December 1, 2007, 8:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package costanza;

/**
 * PeakRemover removes a peak if its smaller than some threshold size,
 * or if has an intensity lower than some threshold.
 *
 * @author pontus
 */
public class PeakRemover extends Processor {

	/** Creates a new instance of PeakRemover */
	public PeakRemover() {
	}

	/**
	 * Implements the PeakRemover algorithm.
	 * @param c a Case 
	 * @param o an Option containing two thresholds for size and intensity.
	 * @return a modified Case object
	 * @see Processor
	 */
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
				c.removeAllCellData(cc.getCell());
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
				c.removeAllCellData(boa.getCell());
			}
		}
		return c;
	}

}
