package Costanza;
import java.util.Vector;
import java.lang.Exception;
import java.util.Iterator;


/**
 * Costanzas representation of the stack of images.
 * Basically it's just a Vector of Images, with a bunch of wrapper methods.
 *
 */
public class Stack {
    
    /**depth in z-direction.*/
    private int depth;
    
    /**Height of the image, taken from the Image class.*/
    private int height;
    
    /**Width of the image, taken from Image class.*/
    private int width;
    
    /** Contains scale of image in x,y z direction.*/
    private float[] scale = {1.0f, 1.0f, 1.0f};
    
    /** Internal representation of the stack.*/
    private Vector<Image> myImage;
    
    /** Stores the maximal intensity in the stack*/
    private float maxIntensity;
    
    /** Stores the minimal intensity in the stack*/
    private float minIntensity;
    
    /** Stores the maximal allowed intensity, default is 1.0*/
    private float maxIntensityLimit=1.0f;
    
    public Stack() {
        myImage = new Vector<Image>();
    }
    
    public Object clone() {
        Stack tmp = new Stack();
        tmp.setHeight(height);
        tmp.setWidth(width);
        tmp.setDepth(depth);
        tmp.setXScale(scale[0]);
        tmp.setYScale(scale[1]);
        tmp.setZScale(scale[2]);
        Vector<Image> copy = new Vector<Image>();
        int numInStack = myImage.size();
        copy.setSize(numInStack);
        for (int i=0; i < numInStack; ++i)
            copy.add(i, (Image) myImage.get(i).clone());
        tmp.setImageVector(copy);
        return tmp;
    }
    
    public Image getImage(int index) {
        return myImage.get(index);
    }
    
    /**
     * Member function returning the depth of the stack.
     * @return depth of stack
     */
    public int getDepth() {
        return depth;
    }
    
    /**
     * Member function returning the height of the stack.
     * @return height of stack
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Member function returning the width of the stack.
     * @return width of stack
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets scale in x-direction.
     * @return scale factor in the x-direction
     */
    public float getXScale() {
        return scale[0];
    }
    
    /**
     * Gets scale in y-direction.
     * @return scale factor in the y-direction
     */
    public float getYScale() {
        return scale[1];
    }
    
    /**
     * Gets scale in x-direction.
     * @return scale factor in the z-direction
     */
    public float getZScale() {
        return scale[2];
    }
    
    /**
     * Gets the scale vector.
     * @return a 3D float array
     */
    public float[] getScale() {
        return scale;
    }
    /**
     * Gets the maximum intensity.
     * @return the intensity maximum
     */
    public float getMaxIntensity() {
        return maxIntensity;
    }
    
    /**
     * Gets the maximum intensity.
     * @return the intensity minimum
     */
    public float getMinIntensity() {
        return minIntensity;
    }
    
    /**
     * Gets the maximum allowed intensity
     * @return the maximum intensity limit
     */
    public float getMaxIntensityLimit() {
        return maxIntensityLimit;
    }
    
    /**
     * Gets the intensity of a point (x,y,z) in the pixel space.
     * @param a point (x,y,z) in pixel space.
     * @return intensity in point (x,y,z)
     */
    public float getIntensity(int x, int y, int z) {
        return myImage.elementAt(z).getIntensity(x,y);
    }
    
    
    
    /**
     * Adds an image to the stack. If the stack is empty it sets the
     * height and width fields, otherwise it checks so that the image
     * has correct dimension and sets the minimum and maximum intensities.
     * @param An Image to add to the stsck.
     */
    public void addImage(Image I) throws Exception {
        if (myImage.isEmpty()) {
            height = I.getHeight();
            width = I.getWidth();
            maxIntensity = I.getMaxIntensity();
            minIntensity = I.getMinIntensity();
        } else if (I.getHeight() != height && I.getWidth() != width){
            throw new Exception("image height and width must be the same for each image in the stack.");
        } else if ( I.getMaxIntensity() > maxIntensity ) {
            maxIntensity= I.getMaxIntensity();
        } else if ( I.getMinIntensity() > minIntensity ) {
            minIntensity= I.getMinIntensity();
        }
        if (maxIntensity > 1.0f && minIntensity < 0.0f) {
            throw new Exception("Intensity is out of range");
        }
        myImage.addElement(I);
        depth= myImage.size();
    }
    
    /**
     * Sets the intensity at point (x,y,z) in pixel space.
     * @param a point (x,y,z) in pixel space and a floating point value.
     */
    public void setIntensity(int x, int y, int z, float value) {
        myImage.elementAt(z).setIntensity(x,y,value);
    }
    
    /**
     * Sets x-scale of Stack.
     * @param new scale value.
     */
    public void setXScale(float s) {
        scale[0]=s;
    }
    
    /**
     * Sets y-scale of Stack.
     * @param new scale value.
     */
    public void setYScale(float s) {
        scale[1]=s;
    }
    
    /**
     * Sets z-scale of Stack.
     * @param new scale value.
     */
    public void setZScale(float s) {
        scale[2]=s;
    }
    
    /**
     * Sets height of stack.
     * @param new height h.
     */
    private void setHeight(int h) {
        height = h;
    }
    
    /**
     * Sets width of stack.
     * @param new width w.
     */
    private void setWidth(int w) {
        width = w;
    }
    
    /**
     * Sets depth of stack.
     * @param new depth d.
     */
    private void setDepth(int d) {
        depth = d;
    }
    
    /**
     * Sets the Vector of images.
     * @param a Vector<Image>
     */
    private void setImageVector(Vector<Image> stack) {
        myImage = stack;
    }
}
