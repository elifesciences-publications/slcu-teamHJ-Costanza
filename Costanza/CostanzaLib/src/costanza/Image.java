package costanza;

import java.awt.Graphics;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;

/** Container class for pixel information */
public class Image {

    /** A BufferedImage representing a Greyscale channel. */
    BufferedImage bi;
    final int imageType = BufferedImage.TYPE_BYTE_GRAY;
	    

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
     * @param bi the BufferedImage to construct our Image from.
     */
    public Image(java.awt.Image image) {
	if (image instanceof BufferedImage) {
	    this.bi = (BufferedImage) image;
	}else{
	    this.bi = new BufferedImage(image.getWidth(null), image.getHeight(null), imageType);
	    Graphics g = bi.getGraphics();
	    g.drawImage(image, 0, 0, null);
	    //throw new Exception("AWT Image sent is not of BufferedImage instance.");
	}
    }

    /**Implementation of the Objects clone method.
     * Naturally this clones our Object.
     */
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

    /** Returns the width of the image. */
    public int getWidth() { return bi.getWidth(); }

    /** Returns the width of the image. */
    public int getHeight() { return bi.getHeight(); }

    /** Returns the intensity for pixel at position (x, y). */
    public float getIntensity(int x, int y) {
	return bi.getRaster().getSampleFloat(x, y, 0);
    }

    /** Sets the intensity for pixel at position (x, y). */
    public void setIntensity(int x, int y, float value) {
	bi.getRaster().setSample(x, y, 0, value);
    }

    /**Sets the Vector of pixels.
     * @param pixelVector The float array of pixels we want to set
     */
    private void setPixelVector(float[] pixelVector) {
	throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Returns the maximum intensity of the image. */
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

    /** Returns the minimum intensity of the image. */
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
}
