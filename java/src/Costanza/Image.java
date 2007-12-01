package Costanza;

import java.lang.Float;
import java.util.Vector;

/** Container class for pixel information */
public class Image {    
    /** Greyscale channel */
    private Vector<Float> pixels;
    
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
        pixels = new Vector<Float>(width * height);
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
 
    /** Gets the intensity for pixel at position (x, y) */
    public float getIntensity(int x, int y) {
        Float element = pixels.elementAt(x + y * width);
        return element.floatValue();
    }
    
    /** Sets the intensity for pixel at position (x, y) */
    public void setIntensity(int x, int y, float value) {
        Float element(value);
        pixels.elementAt(x + y * width) = element;
    }
    
    public float getMaxIntensity() {
        // TODO
        return 0.0f;
    }
    
    public float getMinIntensity() {
        // TODO
        return 0.0f;
    }
    
}
