package costanza;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

/** This class Marks red, green and blue pixels in a BufferedImage.
 *
 * @author michael
 */
class PixelMarker {

    private BufferedImage image;
    /** The image to plot the coloured pixels to. */
    private Vector<Pixel> r;
    /** The list of red pixels to plot. */
    private Vector<Pixel> g;
    /** The list of green pixels to plot. */
    private Vector<Pixel> b;

    /**The list of blue pixels to plot. */
    /**Constructor for class PixelMarker.
     * The image sent to this constructor is NOT copied so all pixels
     * will be plotted to this image.
     * @param image the image to mark pixels in.
     * @param r the pixels we should mark with red.
     * @param g the pixels we should mark with green.
     * @param b the pixels we should mark with blue.
     */
    PixelMarker(BufferedImage image, Vector<Pixel> r, Vector<Pixel> g, Vector<Pixel> b) {
	this.image = image;
	this.r = (r == null) ? new Vector<Pixel>() : r;
	this.g = (g == null) ? new Vector<Pixel>() : g;
	this.b = (b == null) ? new Vector<Pixel>() : b;
    }

    /**Constructor for class PixelMarker.
     * The image sent to this constructor is copied so all pixels
     * will be plotted to a new image. So in order to get the new image 
     * you will have to call getImage method.
     * @param image the image to mark pixels in.
     * @param r the pixels we should mark with red.
     * @param g the pixels we should mark with green.
     * @param b the pixels we should mark with blue.
     */
    PixelMarker(Image image, Vector<Pixel> r, Vector<Pixel> g, Vector<Pixel> b) {
	this.image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
	boolean drawImage = this.image.getGraphics().drawImage(image.getImage(), 0, 0, null);
	if (!drawImage) {
	    System.err.println("PixelMarker: Failed to write to new BufferedImage.");
	}
	this.r = (r == null) ? new Vector<Pixel>() : r;
	this.g = (g == null) ? new Vector<Pixel>() : g;
	this.b = (b == null) ? new Vector<Pixel>() : b;
    }

    /** Returns the image.
     * 
     * @return the image.
     */
    BufferedImage getImage() {
	return image;
    }

    /** Marks all the red, green and blue pixels in this image.
     * 
     * @return the image marked with the red, green and blue pixels.
     */
    public BufferedImage markAllPixels() {
	int red = (255 << 16) & 0xff0000;
	int green = (255 << 8) & 0x00ff00;
	int blue = 255 & 0x0000ff;

	for (Iterator<Pixel> it = r.iterator(); it.hasNext();) {
	    Pixel pixel = it.next();
	    image.setRGB(pixel.getX(), pixel.getY(), red);
	}
	for (Iterator<Pixel> it = g.iterator(); it.hasNext();) {
	    Pixel pixel = it.next();
	    image.setRGB(pixel.getX(), pixel.getY(), green);
	}
	for (Iterator<Pixel> it = b.iterator(); it.hasNext();) {
	    Pixel pixel = it.next();
	    image.setRGB(pixel.getX(), pixel.getY(), blue);
	}
	return image;
    }
}
