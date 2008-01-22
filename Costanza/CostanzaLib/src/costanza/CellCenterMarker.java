package costanza;

import java.awt.image.BufferedImage;
import java.util.Collection;

/**Implementation of a Processor that marks each CellCenter with red redColor.
 * @author michael
 * @see Processor
 */
public class CellCenterMarker extends Processor {

    /**This colors the CellCenter using a red redColor. 
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
	int redColor = 0xffff0000;
	for (int i = 0; i < ccs.length; ++i) {
	    CellCenter cc = (CellCenter) ccs[i];
	    BufferedImage bufferedImage = images[cc.getZ()];
	    //Color the center and the neighbouring pixels red.

	    if (cc.getX() - 1 > 0 &&
		    cc.getX() + 1 < bufferedImage.getWidth() &&
		    cc.getY() - 1 > 0 &&
		    cc.getY() + 1 < bufferedImage.getHeight()) {
		bufferedImage.setRGB(cc.getX() - 1, cc.getY() - 1, redColor);
		bufferedImage.setRGB(cc.getX(), cc.getY() - 1, redColor);
		bufferedImage.setRGB(cc.getX() + 1, cc.getY() - 1, redColor);
		bufferedImage.setRGB(cc.getX() - 1, cc.getY(), redColor);
		bufferedImage.setRGB(cc.getX(), cc.getY(), redColor);
		bufferedImage.setRGB(cc.getX() + 1, cc.getY(), redColor);
		bufferedImage.setRGB(cc.getX() - 1, cc.getY() + 1, redColor);
		bufferedImage.setRGB(cc.getX(), cc.getY() + 1, redColor);
		bufferedImage.setRGB(cc.getX() + 1, cc.getY() + 1, redColor);
	    } else {
		bufferedImage.setRGB(cc.getX(), cc.getY(), redColor);
	    }
	}
	c.setResultImages(images);

	System.out.println("Number of collected Cell Centers: " + cellCenters.size());
	return c;
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
