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
	 * @param a Case and an Option containing two
	 * thresholds for size and intensity.
	 * @returns a modified Case object
	 * @see Processor
	 */
	public Case process(Case c, Options o) throws Exception {
		float sizeThreshold = ((Float) o.getOptionValue("sizeThreshold")).floatValue();
		float intensityThreshold = ((Float) o.getOptionValue("intensityThreshold")).floatValue();

		Object[] centers = c.getData().getData(DataId.cellCenters).toArray();
		for (int i = 0; i < centers.length; ++i) {
			CellCenter cc = (CellCenter) centers[i];
			int x = cc.getX();
			int y = cc.getY();
			int z = cc.getZ();
			if (c.getStack().getIntensity(x, y, z) < intensityThreshold) {
				c.getManipulator().removeAll(cc.getId());
			}
		}
		Object[] boas = c.getData().getData(DataId.cellBasinsOfAttraction).toArray();
		for (int i = 0; i < boas.length; ++i) {
			BOA boa = (BOA) boas[i];//it.next();
			float size =
					boa.getPixels().size() *
					(c.getStack().getXScale()) *
					(c.getStack().getYScale()) *
					(c.getStack().getZScale());
			if (size < sizeThreshold) {
				c.getManipulator().removeAll(boa.getId());
			}
		}
		return c;
	}

}
