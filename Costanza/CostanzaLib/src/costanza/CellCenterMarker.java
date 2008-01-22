package costanza;

import java.awt.image.BufferedImage;
import java.util.Collection;

/**Implementation of a Processor that colors each BOA with a unique color.
 * @author michael
 * @see Processor
 */
public class CellCenterMarker extends Processor {

    /**This colors the CellCenter using a red color. 
     * @param c the Case to process.
     * @param options not used in this Processor.
     * @return the processed Case.
     * @throws java.lang.Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Case process(Case c, Options options) throws Exception {
	// Get the basin of attractors from data in case
	Collection<CellCenter> cellCenters = (Collection<CellCenter>) c.getCellData(DataId.CENTERS);
	if (cellCenters == null) {
	    return c;
	}
	BufferedImage[] images = getImagesFromStack(c.getOriginalStack());
	Object[] ccs = cellCenters.toArray();
	int color = genRedColor();
	for (int i = 0; i < ccs.length; ++i) {
	    CellCenter cc = (CellCenter) ccs[i];
	    BufferedImage bufferedImage = images[cc.getZ()];
	    bufferedImage.setRGB(cc.getX(), cc.getY(), color);
	}
	c.setResultImages(images);

	System.out.println("Number of collected Cell Centers: " + cellCenters.size());
	return c;
    }

    /**Generate a red color encoded in a 4 byte int as ARGB with alpha set to max.
     * @return the red color encoded in a 4 byte int as ARGB with alpha set to max.
     */
    private int genRedColor() {
	return 0xffff0000;
    }

    /**Convert the Stack to an array of BufferedImage that we can manipulate.
     * @param stack the Stack to extract the images from.
     * @return an array of BufferedImage representing the Images in our Stack.
     */
    private BufferedImage[] getImagesFromStack(Stack stack) {
	BufferedImage[] images = new BufferedImage[stack.getDepth()];
	for (int i = 0; i < images.length; i++) {
	    images[i] = stack.getImage(i).getImage();
	}
	return images;
    }
}
