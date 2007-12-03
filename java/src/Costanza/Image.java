package Costanza;

import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/** Container class for pixel information */
public class Image {
    
    /** A BufferedImage representing a Greyscale channel */
    BufferedImage bi;
    
    /** Width of image. */
    private int width;
    
    /** Height of image. */
    private int height;
    
    /** Constructor for class Image.
     *
     * This is the constructor that should be used to create a new Image object. It sets the width and height of the image.
     *
     */
    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    }
    
    /** Constructor for class Image from an AWT Image.
     * Note that this does NOT copy the BufferedImage! Any changes made 
     * to Image will affect the BufferedImage as well.
     * @param bi the BufferedImage to construct our Image from.
     */
    public Image(BufferedImage bi) {
        width = bi.getWidth();
        height = bi.getHeight();
        this.bi = bi;        
    }
    
    public Object clone() {
        Image newImage = new Image(bi.getWidth(), bi.getHeight());
        WritableRaster raster = bi.copyData( null );
        BufferedImage copy = new BufferedImage( bi.getColorModel(), raster, bi.isAlphaPremultiplied(), null );
        newImage.setBufferedImage(copy);
        /*try {
            ImageIO.write(copy, "jpg", new File("apa1.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        return newImage;
    }
    
    /** Returns the width of the image. */
    public int getWidth() { return width; }
    
    /** Sets the width of the image. */
    public void setWidth(int width) { this.width = width; }
    
    /** Returns the width of the image. */
    public int getHeight() { return height; }
    
    /** Sets the height of the image. */
    public void setHeight(int height) { this.height = height; }
    
    /** Returns the intensity for pixel at position (x, y). */
    public float getIntensity(int x, int y) { return bi.getRaster().getSampleFloat(x,y,0); }
    
    /** Sets the intensity for pixel at position (x, y). */
    public void setIntensity(int x, int y, float value) {
        bi.getRaster().setSample(x,y,0,value);
    }
    
    /**
     * Sets the Vector of pixels.
     * @param pixelVector The float array of pixels we want to set
     */
    private void setPixelVector(float[] pixelVector) { throw new UnsupportedOperationException("Not yet implemented"); }
    
    
    /** Returns the maximum intensity of the image. */
    public float getMaxIntensity() {
        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < bi.getWidth(); ++i) {
            for (int j = 0; j < bi.getHeight(); j++) {
                if (getIntensity(i,j) > max) { max = getIntensity(i,j); }
            }
        }
        return max;
    }
    
    /** Returns the minimum intensity of the image. */
    public float getMinIntensity() {
        float min = Float.POSITIVE_INFINITY;
        for (int i = 0; i < bi.getWidth(); ++i) {
            for (int j = 0; j < bi.getHeight(); j++) {
                if (getIntensity(i,j) < min) { min = getIntensity(i,j); }
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
    public BufferedImage getImage() { return bi; }
    
}
