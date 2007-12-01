package Costanza;

import java.lang.Number;
import java.util.Vector;

/** Container class for pixel information */
public class Image {    
    /** Greyscale channel */
    private Vector<Float> pixels;
    
    /** Width of image */
    private int width;
    
    /** Height of image */
    private int height;
    
    /** Constructor for class Image.
     *
     * This is the constructor that should be used to create a new Image object. It sets the width and height of the image.
     *
     */
    public Image(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    /** Returns the width of the image. */
    public int getWidth() {
        return width;
    }
    
    /** Sets the width of the image. */
    public void setWidth(int width) {
        this.width = width;
    }
    
    /** Returns the width of the image. */
    public int getHeight() {
        return height;
    }
    
    /** Sets the height of the image. */
    public void setHeight(int height) {
        this.height = height;
    }
    
    public float getIntensity(int x, int y) {
        // TODO
        return 0.0;
    }
    
    public void setIntensity(int x, int y, float value) {
        // TODO
    }
    
    public float getMaxIntensity() {
        // TODO
        return 0.0;
    }
    
    public float getMinIntensity() {
        // TODO
        return 0.0;
    }
    
}
