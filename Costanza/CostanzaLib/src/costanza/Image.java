package costanza;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Container class for pixel information */
public class Image {

    /** The floats representing our grayscale image. */
    protected float[] pixels;
    /** The width of our image. */
    private int width;
    /** The height of our image. */
    private int height;

    /** Constructor for class Image.
     * This is the constructor that should be used to create a new Image object. 
     * It sets the width and height of the image.
     */
    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new float[width * height];
    }

    /** Constructor for class Image from an AWT Image.
     * Note that this does NOT copy the BufferedImage! Any changes made 
     * to Image will affect the BufferedImage as well.
     * @param image the BufferedImage to construct our Image from.
     */
    public Image(java.awt.Image image) {
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        this.pixels = new float[width * height];
        int[] iArray = new int[width * height];
        PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, iArray, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException ex) {
            Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Store the red component as a float in our pixels array
        for (int i = 0; i < iArray.length; i++) {
            pixels[i] = (float) (RgbToSamples(iArray[i])[1] / 255.0);
        //int[] argb = RgbToSamples(iArray[i]);
        //System.out.println("Float: " + (int) (pixels[i] * 255) + " Alfa: " + argb[0] + " Red: " + argb[1] + " Green: " + argb[2] + " Blue: " + argb[3]);
        }
    /*
    System.out.println("Pixel(50,50): " + pixels[getIndex(50, 50)]);
    int[] argb = RgbToSamples(iArray[getIndex(50, 50)]);
    System.out.println("Float: " + (int) (pixels[getIndex(50, 50)] * 255) + " Alfa: " + argb[0] + " Red: " + argb[1] + " Green: " + argb[2] + " Blue: " + argb[3]);
    argb = RgbToSamples(intensityToRgb(pixels[getIndex(50, 50)]));
    System.out.println("Float: " + (int) (pixels[getIndex(50, 50)] * 255) + " Alfa: " + argb[0] + " Red: " + argb[1] + " Green: " + argb[2] + " Blue: " + argb[3]);
     */
    }

    /** Constructor for class Image from an AWT BufferedImage.
     * Note that this does NOT copy the BufferedImage! Any changes made 
     * to Image will affect the BufferedImage as well.
     * @param image the BufferedImage to construct our Image from.
     */
    public Image(BufferedImage image) {
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        this.pixels = new float[width * height];
        switch (image.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
                for (int i = 0; i < pixels.length; ++i) {
                    int[] argb = null;
                    argb = image.getRaster().getPixel(i % width, i / width, argb);
                    pixels[i] = (float) (argb[0] / 255.0);
                //System.out.println("Length: " + argb.length);
                    /*int[] argb2 = new int[4];
                argb2[0] = image.getColorModel().getAlpha(argb[0]);
                argb2[1] = image.getColorModel().getRed(argb[0]);
                argb2[2] = image.getColorModel().getGreen(argb[0]);
                argb2[3] = image.getColorModel().getBlue(argb[0]);
                argb = RgbToSamples(argb[0]);
                if (i % width == 50 && i / width == 50) {
                System.out.println("Float: " + (int) (pixels[getIndex(50, 50)] * 255) + " Alfa: " + argb[0] + " Red: " + argb[1] + " Green: " + argb[2] + " Blue: " + argb[3]);
                System.out.println("Float: " + (int) (pixels[getIndex(50, 50)] * 255) + " Alfa: " + argb2[0] + " Red: " + argb2[1] + " Green: " + argb2[2] + " Blue: " + argb2[3]);
                }
                 */
                }
                break;
            case BufferedImage.TYPE_INT_RGB:
                try {
                    PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, false);
                    pg.grabPixels();
                    int[] iArray = (int[]) pg.getPixels();
                    for (int i = 0; i < iArray.length; i++) {
                        pixels[i] = (float) (RgbToSamples(iArray[i])[1] / 255.0);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            default:
        }
    }

    /**Implementation of the Objects clone method.
     * Naturally this clones our Object.
     * @return a clone of the current object.
     */
    @Override
    public Object clone() {
        Image newImage = new Image(getWidth(), getHeight());
        newImage.pixels = new float[getWidth() * getHeight()];
        System.arraycopy(pixels, 0, newImage.pixels, 0, pixels.length);
        return newImage;
    }

    float[] getPixels(){
        return pixels;
    }
    
    /** Returns the width of the image.
     * @return the width of the image.
     */
    public int getWidth() {
        return width;
    }

    /** Returns the width of the image. 
     * @return the height of the image.
     */
    public int getHeight() {
        return height;
    }

    /** Returns the intensity for pixel at position (x, y).
     * 
     * @param x the horizontal coordinate.
     * @param y the vertical coordinate.
     * @return the intensity located at (x,y).
     */
    public float getIntensity(int x, int y) {
        return pixels[getIndex(x, y)];
    }

    /** Sets the intensity for pixel at position (x, y).
     * 
     * @param x the horizontal coordinate.
     * @param y the vertical coordinate.
     * @param value the intensity to set.
     */
    public void setIntensity(int x, int y, float value) {
        pixels[getIndex(x, y)] = value;
    }

    /**Sets the Vector of intPixels.
     * 
     * @param pixels The float array of grayscales we want to set
     */
    private void setPixels(float[] pixels) {
        this.pixels = pixels;
    }

    /** Returns the maximum intensity of the image.
     * 
     * @return the maximum intensity within this Image.
     */
    public float getMaxIntensity() {
        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < pixels.length; ++i) {
            if (pixels[i] > max) {
                max = pixels[i];
            }
        }
        return max;
    }

    /** Returns the minimum intensity of the image.
     * 
     * @return the minimum intensity within this Image.
     */
    public float getMinIntensity() {
        float min = Float.POSITIVE_INFINITY;
        for (int i = 0; i < pixels.length; ++i) {
            if (pixels[i] < min) {
                min = pixels[i];
            }
        }
        return min;
    }

    /** Returns a BufferedImage representing this Image.
     * @return a BufferedImage representation of this Image.
     */
    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < getWidth(); ++i) {
            for (int j = 0; j < getHeight(); ++j) {
                image.setRGB(i, j, intensityToRgb(pixels[getIndex(i, j)]));
            }
        }
        /*
        System.out.println("Pixel(50,50): " + pixels[getIndex(50, 50)]);
        int[] argb = RgbToSamples(image.getRGB(50, 50));
        System.out.println("Float: " + (int) (pixels[getIndex(50, 50)] * 255) + " Alfa: " + argb[0] + " Red: " + argb[1] + " Green: " + argb[2] + " Blue: " + argb[3]);
        argb = RgbToSamples(intensityToRgb(pixels[getIndex(50, 50)]));
        System.out.println("Float: " + (int) (pixels[getIndex(50, 50)] * 255) + " Alfa: " + argb[0] + " Red: " + argb[1] + " Green: " + argb[2] + " Blue: " + argb[3]);
         */
        return image;
    }

    /** Create a new BufferedImage with the given intPixels marked.
     * 
     * @param r the intPixels that should be marked in red.
     * @param g the intPixels that should be marked in green.
     * @param b the intPixels that should be marked in blue.
     * @return the new marked BufferedImage.
     */
    public BufferedImage markPixels(Vector<Pixel> r, Vector<Pixel> g, Vector<Pixel> b) {
        PixelMarker pm = new PixelMarker(this, r, g, b);
        return pm.markAllPixels();
    }

    /**Convert x and y coordinate to index.
     * 
     * @param x the x coordinate to use.
     * @param y the y coordinate to use.
     * @return the index.
     */
    private int getIndex(int x, int y) {
        return y * width + x;
    }

    /** Convert the greyscale intensity to an RGB int.
     * 
     * @param intensity the intensity to use.
     * @return the intensity in RGB encoding.
     */
    private int intensityToRgb(float intensity) {
        int tmp = (int) (intensity * 255.0);
        int pixel = (255 << 24) | (tmp << 16) | (tmp << 8) | (tmp);
        return pixel;
    }

    /** Get the red green and blue components out from the packed rgb int.
     * 
     * @param rgb the int containing the alpha, red, green and blue components.
     * @return an int array of the components.
     */
    private int[] RgbToSamples(int rgb) {
        int alpha = (rgb >> 24) & 0xff;
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = rgb & 0xff;
        return new int[]{alpha, red, green, blue};
    }
}
