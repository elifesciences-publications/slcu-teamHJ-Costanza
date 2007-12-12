package costanza;

import java.awt.color.ColorSpace;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Vector;

/** Container class for pixel information */
public class Image {

    /** A BufferedImage representing a Greyscale channel. */
    BufferedImage bi;
    final int imageType = BufferedImage.TYPE_INT_RGB;
    //final int imageType = BufferedImage.TYPE_BYTE_GRAY;

    /** Constructor for class Image.
     * This is the constructor that should be used to create a new Image object. 
     * It sets the width and height of the image.
     */
    public Image(int width, int height) {
	bi = new BufferedImage(width, height, imageType);
    }

    /** Constructor for class Image from an AWT Image.
     * Note that this does NOT copy the BufferedImage! Any changes made 
     * to Image will affect the BufferedImage as well.
     * @param image the BufferedImage to construct our Image from.
     */
    public Image(java.awt.Image image) {
	if (image instanceof BufferedImage) {
	    this.bi = (BufferedImage) image;
	} else {
	    //ColorSpace cs = image.getGraphics().getColor().getColorSpace();
	    //System.out.println("Type of image: " + cs.getType());
	    
	    this.bi = new BufferedImage(image.getWidth(null), image.getHeight(null), imageType);
	    bi.getGraphics().drawImage(image, 0, 0, null);
	}
    }

    /**Implementation of the Objects clone method.
     * Naturally this clones our Object.
     * @return a clone of the current object.
     */
    @Override
    public Object clone() {
	Image newImage = new Image(bi.getWidth(), bi.getHeight());
	WritableRaster raster = bi.copyData(null);
	BufferedImage copy = new BufferedImage(bi.getColorModel(), raster, bi.isAlphaPremultiplied(), null);
	newImage.setBufferedImage(copy);
	/*try {
	ImageIO.write(copy, "jpg", new File("apa1.jpg"));
	} catch (IOException ex) {
	ex.printStackTrace();
	}*/
	return newImage;
    }

    /** Returns the width of the image.
     * @return the width of the image.
     */
    public int getWidth() {
	return bi.getWidth();
    }

    /** Returns the width of the image. 
     * @return the height of the image.
     */
    public int getHeight() {
	return bi.getHeight();
    }

    /** Returns the intensity for pixel at position (x, y).
     * 
     * @param x the horizontal coordinate.
     * @param y the vertical coordinate.
     * @return the intensity located at (x,y).
     */
    public float getIntensity(int x, int y) {
	float value = 0;
	try {
	    value = bi.getRaster().getSampleFloat(x, y, 0);
	} catch (Exception e) {
	    System.out.println("Image width: " + bi.getWidth());
	    System.out.println("Image height: " + bi.getHeight());
	    System.out.println("X: " + x);
	    System.out.println("Y: " + y);
	}
	return value;
    }

    /** Sets the intensity for pixel at position (x, y).
     * 
     * @param x the horizontal coordinate.
     * @param y the vertical coordinate.
     * @param value the intensity to set.
     */
    public void setIntensity(int x, int y, float value) {
	bi.getRaster().setSample(x, y, 0, value);
	//bi.setRGB(x, y, (int)value);
    }

    /**Sets the Vector of pixels.
     * 
     * @param pixelVector The float array of pixels we want to set
     */
    private void setPixelVector(float[] pixelVector) {
	throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Returns the maximum intensity of the image.
     * 
     * @return the maximum intensity within this Image.
     */
    public float getMaxIntensity() {
	float max = Float.NEGATIVE_INFINITY;
	for (int i = 0; i < bi.getWidth(); ++i) {
	    for (int j = 0; j < bi.getHeight(); j++) {
		if (getIntensity(i, j) > max) {
		    max = getIntensity(i, j);
		}
	    }
	}
	return max;
    }

    /** Returns the minimum intensity of the image.
     * 
     * @return the minimum intensity within this Image.
     */
    public float getMinIntensity() {
	float min = Float.POSITIVE_INFINITY;
	for (int i = 0; i < bi.getWidth(); ++i) {
	    for (int j = 0; j < bi.getHeight(); j++) {
		if (getIntensity(i, j) < min) {
		    min = getIntensity(i, j);
		}
	    }
	}
	return min;
    }

    /** Sets the BufferedImage in our Image.
     * @param bufferedImage the BufferedImage to set.
     */
    public void setBufferedImage(BufferedImage bufferedImage) {
	bi = bufferedImage;
    }

    /** Returns the current BufferedImage in this Image.
     * @return the current BufferedImage stored in this Image.
     */
    public BufferedImage getImage() {
	return bi;
    }

    /** Create a new BufferedImage with the given pixels marked.
     * 
     * @param r the pixels that should be marked in red.
     * @param g the pixels that should be marked in green.
     * @param b the pixels that should be marked in blue.
     * @return the new marked BufferedImage.
     */
    public BufferedImage markPixels(Vector<Pixel> r, Vector<Pixel> g, Vector<Pixel> b) {
	PixelMarker pm = new PixelMarker(this, r, g, b);
	return pm.markAllPixels();
    }

    /** Convert the greyscale intensity to an RGB int.
     * 
     * @param i the intensity to use.
     * @return the intensity in RGB encoding.
     */
    private int intensityToRgb(int i) {
	return 0;
    }
}
