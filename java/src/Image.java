import java.lang.Number;
import java.util.Vector;

/** Container class for pixel information */
public class Image
{
    /** Greyscale channel */
    private Vector<Float> grey;

    /** Width of image */
    private int width;

    /** Height of image */
    private int height;

    /** Sets the width of the image. */
    public void setWidth(int width) {
	this.width = width;
    }

    /** Returns the width of the image. */
    public int getWidth() {
	return width;
    }

    /** Sets the height of the image. */
    public void setHeight(int height) {
	this.height = height;
    }

    /** Returns the width of the image. */
    public int getHeight() {
	return height;
    }
}
