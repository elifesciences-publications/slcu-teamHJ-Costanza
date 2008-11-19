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
        int MARK_NEIGHBORS = 0;
        if(options != null && options.hasOption("markNeighbors"))
            MARK_NEIGHBORS = ((Integer)options.getOptionValue("markNeighbors")).intValue();
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
            
            if ( MARK_NEIGHBORS != 0){
                for( int x = cc.getX() - MARK_NEIGHBORS; x <= cc.getX() + MARK_NEIGHBORS; ++x )
                {
                    if(x < bufferedImage.getWidth() &&  x > 0){
                        for( int y = cc.getY() - MARK_NEIGHBORS; y <= cc.getY() + MARK_NEIGHBORS; ++y ){
                            if(y < bufferedImage.getHeight() &&  y > 0){
                                bufferedImage.setRGB(x,y,redColor);
                            }
                        }
                    }
                }
            }
            else
            {
                bufferedImage.setRGB(cc.getX(), cc.getY(), redColor);
            }
//	    if ( MARK_NEIGHBORS != 0 && cc.getX() - MARK_NEIGHBORS > 0 &&
//		    cc.getX() + MARK_NEIGHBORS < bufferedImage.getWidth() &&
//		    cc.getY() - MARK_NEIGHBORS > 0 &&
//		    cc.getY() + MARK_NEIGHBORS < bufferedImage.getHeight()) {
//		bufferedImage.setRGB(cc.getX() - MARK_NEIGHBORS, cc.getY() - MARK_NEIGHBORS, redColor);
//		bufferedImage.setRGB(cc.getX(), cc.getY() - MARK_NEIGHBORS, redColor);
//		bufferedImage.setRGB(cc.getX() + MARK_NEIGHBORS, cc.getY() - MARK_NEIGHBORS, redColor);
//		bufferedImage.setRGB(cc.getX() - MARK_NEIGHBORS, cc.getY(), redColor);
//		
//		bufferedImage.setRGB(cc.getX() + MARK_NEIGHBORS, cc.getY(), redColor);
//		bufferedImage.setRGB(cc.getX() - MARK_NEIGHBORS, cc.getY() + MARK_NEIGHBORS, redColor);
//		bufferedImage.setRGB(cc.getX(), cc.getY() + MARK_NEIGHBORS, redColor);
//		bufferedImage.setRGB(cc.getX() + MARK_NEIGHBORS, cc.getY() + MARK_NEIGHBORS, redColor);
//            }
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
