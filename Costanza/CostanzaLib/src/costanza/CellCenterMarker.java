package costanza;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

;

/**Implementation of a Processor that colors each BOA with a unique color.
 * @author michael
 * @see Processor
 */
public class CellCenterMarker extends Processor {

    @Override @SuppressWarnings("unchecked")
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

    private int combine(int color1, int color2) {
	int red = (((color1 >> 16) & 0xff) + ((color2 >> 16) & 0xff)) / 2;
	int green = (((color1 >> 8) & 0xff) + ((color2 >> 8) & 0xff)) / 2;
	int blue = (((color1) & 0xff) + ((color2) & 0xff)) / 2;
	return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

    private int genRedColor() {
	int color = 0;
	int red = 255;
	int green = 0;
	int blue = 0;
	//color = (255 & 0xff << 24) | (red & 0xff << 16) | (green << 8) | blue;
	color = 0xffff0000;
	return color;
    }

    private BufferedImage[] getImagesFromStack(Stack stack) {
	BufferedImage[] images = new BufferedImage[stack.getDepth()];
	for (int i = 0; i < images.length; i++) {
	    images[i] = stack.getImage(i).getImage();
	}
	return images;
    }

    private boolean isGrayScale(int rgb) {
	int red = (rgb >> 16) & 0xff;
	int green = (rgb >> 8) & 0xff;
	int blue = rgb & 0xff;
	return red == green && red == blue;
    }
}
