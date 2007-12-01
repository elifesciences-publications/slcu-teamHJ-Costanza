package Costanza;

import java.lang.Float;
import java.util.Vector;
import java.util.Iterator;

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
        pixels = new Vector<Float>();
        pixels.setSize(width * height);
    }
    
    public Object clone() {
        Image tmp = new Image(width, height);
        Iterator<Float> it = pixels.iterator();
        Vector<Float> copy = new Vector<Float>();
        //copy.setSize(numPixel);
        for (int i=0; i < pixels.size(); ++i)
            copy.add(pixels.get(i));
        tmp.setPixelVector(copy);
        return tmp;
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
    
    /** Returns the intensity for pixel at position (x, y). */
    public float getIntensity(int x, int y) {
        Float element = pixels.elementAt(x + y * width);
        return element.floatValue();
    }
    
    /** Sets the intensity for pixel at position (x, y). */
    public void setIntensity(int x, int y, float value) {
        pixels.setElementAt(new Float(value), x + y * width);
    }
    
    
    /**
     * Sets the Vector of pixels.
     * @param a Vector<Float>
     */
    private void setPixelVector(Vector<Float> f) {
        pixels=f;
    }
    
    /** Returns the maximum intensity of the image. */
    public float getMaxIntensity() {
        float max = Float.NEGATIVE_INFINITY;
        int e = pixels.size();
        for (int i = 0; i < e; ++i) {
            Float element = pixels.elementAt(i);
            float value = element.floatValue();
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    
    /** Returns the minimum intensity of the image. */
    public float getMinIntensity() {
        float min = Float.POSITIVE_INFINITY;
        int e = pixels.size();
        for (int i = 0; i < e; ++i) {
            Float element = pixels.elementAt(i);
            float value = element.floatValue();
            if (value < min) {
                min = value;
            }
        }
        return min;
    }
}
