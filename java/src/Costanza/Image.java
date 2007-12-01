package Costanza;

import java.util.Vector;
import java.util.Iterator;

/** Container class for pixel information */
public class Image {
    /** Greyscale channel */
    private float[] pixels;
    
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
        pixels = new float[width*height];
        for(int i=0; i<width*height; ++i) pixels[i] = 0.0f;
    }
    
    public Object clone() {
        Image tmp = new Image(width, height);
        float[] tmpArray = new float[width*height];
        System.arraycopy(pixels, 0, tmpArray, 0, pixels.length);
        tmp.setPixelVector(tmpArray);
        return tmp;
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
    public float getIntensity(int x, int y) { return pixels[x + y * width]; }
    
    /** Sets the intensity for pixel at position (x, y). */
    public void setIntensity(int x, int y, float value) {
        pixels[x + y * width] = value;
    }
    
    /**
     * Sets the Vector of pixels.
     * @param pixelVector The float array of pixels we want to set
     */
    private void setPixelVector(float[] pixelVector) { pixels = pixelVector; }
    
    /** Returns the maximum intensity of the image. */
    public float getMaxIntensity() {
        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < pixels.length; ++i) {
            if (pixels[i] > max) { max = pixels[i]; }
        }
        return max;
    }
    
    /** Returns the minimum intensity of the image. */
    public float getMinIntensity() {
        float min = Float.POSITIVE_INFINITY;
        for (int i = 0; i < pixels.length; ++i) {
            if (pixels[i] < min) {  min = pixels[i]; }
        }
        return min;
    }
}
