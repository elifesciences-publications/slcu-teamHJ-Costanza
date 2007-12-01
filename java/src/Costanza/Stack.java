package Costanza;
import java.util.Vector;


/**
 *  Stack contains the stack of images.
 */
public class Stack {
    /**depth in z-direction.*/
    private int depth;
    /**Height of the image, taken from the Image class.*/
    private int height;
    /**Width of the image, taken from Image class.*/
    private int width;
    /** Contains scale of image in x,y z direction.*/
    private Vector<Float> scale;
    /** Internal representation of the stack.*/
    private Vector<Image> myImage;
    
    private float maxIntensity;
    private float minIntensity;
    private float maxIntensityLimit=1.0f;
    
    Stack() {
        
    }
     /**
     * Member function returning the depth of the stack.
     * @return depth*/
    int getDepth() {
        return depth;
    }
    
    /**
     * Member function returning the height of the stack.
     * @return depth*/
    int getHeight() {
        return height;
    }
    
    /**
     * Member function returning the width of the stack.
     * @return width*/
    int getWidth() {
        return width;
    }
    
    /** Gets scale in x-direction
    * @return scale factor in the x-direction    
    */
    float getXScale() {
        return scale.elementAt(0).floatValue();
    }
    
    /** Gets scale in y-direction
     * @return scale factor in the y-direction
     */
    float getYScale() {
        return scale.elementAt(1).floatValue();
    }
    
    /** Gets scale in x-direction
     * @return scale factor in the z-direction
     */
    float getZScale() {
        return scale.elementAt(2).floatValue();
    }
    
    /** Gets the maximum intensity
     * @return the intensity maximum
     */
    float getMaxIntensity() {
     return maxIntensity;   
    }
    
    /** Gets the maximum intensity
     * @return the intensity minimum
     */
    float getMinIntensity() {
     return minIntensity;   
    }
    
    /** Gets the maximum allowed intensity
     * @return the maximum intensity limit
     */
    float getMaxIntensityLimit() {
     return maxIntensityLimit;   
    }
    
    float getIntensity(int x, int y, int z) {
        return myImage.elementAt(z).getIntensity(x,y);
    }
    
    
    
    /** Adds an image to the stack. If the stack is empty it sets the height and width fields,
     *otherwise it checks so that the image has correct dimension.
     *@param Image
     */
    void addImage(Image I) {
        if (myImage.isEmpty()) {
            height = I.getHeight();
            width = I.getWidth();
            maxIntensity = I.getMaxIntensity();
            minIntensity = I.getMinIntensity();
        } 
        else if (I.getHeight() != height && I.getWidth() != width){
            System.out.println("image height and width must be the same for each image in the stack.");
            System.exit(-1);
        }
        else if ( I.getMaxIntensity() > maxIntensity )
                maxIntensity= I.getMaxIntensity();
        else if ( I.getMinIntensity() > minIntensity )
                minIntensity= I.getMinIntensity();
        
        myImage.add(I);
    }
    
   
    
    
    /** Sets depth*/
    void setDepth(int d) {
        depth = d;
    }
    
    void setIntensity(int x, int y, int z, float value) {
        myImage.elementAt(z).setIntensity(x,y,value);
    }
}
