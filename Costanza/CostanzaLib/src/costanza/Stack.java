package costanza;

import java.util.Vector;

/**
 * Costanzas representation of the stack of images.
 * Basically it's just a Vector of Images, with a bunch of wrapper methods.
 *
 */
public class Stack {

    /**Height of the image, taken from the Image class.*/
    private int height;
    /**Width of the image, taken from Image class.*/
    private int width;
    /** Contains scale of image in x,y z direction.*/
    private float[] scale = {1.0f, 1.0f, 1.0f};
    /** Internal representation of the stack.*/
    private Vector<Image> images;
    /** Stores the maximal intensity in the stack*/
    private float maxIntensity;
    /** Stores the minimal intensity in the stack*/
    private float minIntensity;
    /** Stores the maximal allowed intensity, default is 1.0*/
    private float maxIntensityLimit;

    /**Creates a new empty Stack with the specified width and height.
     * All images added to this Stack must have the same width and height.
     * @param width the width of each image added to this Stack.
     * @param height the height of each image added to this Stack.
     */
    public Stack(int width, int height) {
	this.width = width;
	this.height = height;
	images = new Vector<Image>();
	images.setSize(0);
	maxIntensityLimit = (float) 1.0;
    }

    /**Creates a new empty Stack.
     * 
     */
    public Stack() {
	this.width = 0;
	this.height = 0;
	images = new Vector<Image>();
	images.setSize(0);
	maxIntensityLimit = (float) 1.0;
    }

    /**Constructs a Stack from an array of AWT Images.
     * 
     * @param images the array of images to create the stack from.
     */
    public Stack(java.awt.Image[] images) {
	this.width = 0;
	this.height = 0;
	this.images = new Vector<Image>();
	this.images.setSize(images.length);
	for (int i = 0; i < images.length; ++i) {
	    java.awt.Image image = images[i];
	    this.images.add(new Image(image));
	}
	maxIntensityLimit = (float) 1.0;
    }

    @Override
    public Object clone() {
	//System.out.println("clone: images.size: " + images.size());
	Stack tmp = new Stack(width, height);
	tmp.setXScale(scale[0]);
	tmp.setYScale(scale[1]);
	tmp.setZScale(scale[2]);
	Vector<Image> copy = new Vector<Image>();
	//System.out.println("NumInStack: " + tmp.getDepth());
	//System.out.println("NumInStack: " + images.size());
	//copy.setSize(images.size());
	for (int i = 0; i < images.size(); ++i) {
	    copy.add((Image) images.get(i).clone());
	}
	tmp.setImageVector(copy);
	return tmp;
    }

    public Image getImage(int index) {
	return images.get(index);
    }

    /**
     * Member function returning the depth of the stack.
     * @return depth of stack
     */
    public int getDepth() {
	return images.size(); //depth;
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
     * @param x x-coordinate in in pixel space.
     * @param y y-coordinate in in pixel space.
     * @param z z-coordinate in in pixel space.
     * @return intensity in point (x,y,z)
     */
    public float getIntensity(int x, int y, int z) {
	if (x >= getWidth() || y >= getHeight() || z >= getDepth()) {
	    System.out.println("W: " + getWidth() + " H: " + getHeight() + " D: " + getDepth());
	    System.out.println("X: " + x + " Y: " + y + " Z: " + z);
	}
	return images.elementAt(z).getIntensity(x, y);
    }

    /**
     * Adds an image to the stack. If the stack is empty it sets the
     * height and width fields, otherwise it checks so that the image
     * has correct dimension and sets the minimum and maximum intensities.
     * @param I an Image to add to the stsck.
     */
    public void addImage(Image I) throws Exception {
	if (images.isEmpty()) {
	    height = I.getHeight();
	    width = I.getWidth();
	    maxIntensity = I.getMaxIntensity();
	    minIntensity = I.getMinIntensity();
	} else if (I.getHeight() != height && I.getWidth() != width) {
	    throw new Exception("image height and width must be the same for each image in the stack.");
	} else if (I.getMaxIntensity() > maxIntensity) {
	    maxIntensity = I.getMaxIntensity();
	} else if (I.getMinIntensity() < minIntensity) {
	    minIntensity = I.getMinIntensity();
	}
	/*if (maxIntensity > 1.0f && minIntensity < 0.0f) {
	throw new Exception("Intensity is out of range");
	}*/
	//System.out.println("Size: " + images.size());
	images.addElement(I);
    //System.out.println("Size: " + images.size());
    }

    /**
     * Sets the intensity at point (x,y,z) in pixel space.
     * @param x x-coordinate in in pixel space.
     * @param y y-coordinate in in pixel space.
     * @param z z-coordinate in in pixel space.
     * @param value new intensity value
     */
    public void setIntensity(int x, int y, int z, float value) {
	images.elementAt(z).setIntensity(x, y, value);
    }

    /**
     * Sets x-scale of Stack.
     * @param s new scale value.
     */
    public void setXScale(float s) {
	scale[0] = s;
    }

    /**
     * Sets y-scale of Stack.
     * @param s new scale value.
     */
    public void setYScale(float s) {
	scale[1] = s;
    }

    /**
     * Sets z-scale of Stack.
     * @param s new scale value.
     */
    public void setZScale(float s) {
	scale[2] = s;
    }

    /**
     * Sets height of stack.
     * @param h new height h.
     */
    private void setHeight(int h) {
	height = h;
    }

    /**
     * Sets width of stack.
     * @param w new width w.
     */
    private void setWidth(int w) {
	width = w;
    }

    /**
     * Sets the Vector of images.
     * @param stack a Vector<Image>
     */
    private void setImageVector(Vector<Image> stack) {
	images = stack;
    }
}
