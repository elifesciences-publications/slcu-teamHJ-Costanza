package costanza;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**Implementation of a Processor that colors each BOA with a unique color.
 * @author michael
 * @see Processor
 */
public class BoaColorizer extends Processor {

    /**This colors the BOA using a random color. 
     * The coloring is done on the original Stack and not the working Stack. 
     * @param c the Case to process.
     * @param options not used in this Processor.
     * @return the processed Case.
     * @throws java.lang.Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public Case process(Case c, Options options) throws Exception {
        // Get the basin of attractors from data in case
//	Collection<BOA> boaCollection = (Collection<BOA>) c.getCellData(DataId.BOAS);
//	if (boaCollection == null) {
//	    return c;
//	}
        //System.out.println("Starting BoaColorizer");
        int[] boaColors = genColors(c.sizeOfCells());
        //System.out.println("Colors generated");
        BufferedImage[] images = getImagesFromStack(c.getOriginalStack());
        Object[] cells = c.getCells().toArray();
        if (boaColors.length != cells.length) {
            throw new Exception("Lengths differ!");
        }

        //System.out.println("Colors done.");
        int xDim = images[0].getWidth();
        int yDim = images[0].getHeight();
        int zDim = images.length;
        int curSize = boaColors.length;
        PixelFlag pf = (PixelFlag) c.getStackData(DataId.PIXEL_FLAG);
        int neg_counter = 0;
        for (int iz = 0; iz < zDim; ++iz) {
            BufferedImage bufferedImage = images[iz];
            for (int iy = 0; iy < yDim; ++iy) {
                for (int ix = 0; ix < xDim; ++ix) {
                    int flag = pf.getFlag(ix, iy, iz);
                    if (flag != PixelFlag.BACKGROUND_FLAG) {
                        if (flag < 0)
                            ++neg_counter;
                    }
                }
            }
        }
        //System.out.println( neg_counter + " negative flags");
        //System.out.println("Processing pixel flag.");
        for (int iz = 0; iz < zDim; ++iz) {
            BufferedImage bufferedImage = images[iz];
            for (int iy = 0; iy < yDim; ++iy) {
                for (int ix = 0; ix < xDim; ++ix) {
                    int flag = pf.getFlag(ix, iy, iz);
                    if (flag != PixelFlag.BACKGROUND_FLAG) {
                        if (flag < 0)
                        System.out.println("flag : "+ flag);
                        int color;
                        if (flag >= curSize) {
                            color = randomColor();
                        } else {
                            color = boaColors[flag];
                        }


                        int prevColor = bufferedImage.getRGB(ix, iy);
                        int newColor = color;
                        if (isGrayScale(prevColor) == false) {
                            System.out.println("Combining colors from BOA!");
                            newColor = combine(prevColor, color);
                        }
                        bufferedImage.setRGB(ix, iy, newColor);

                    }
                }
            }
        }

        //System.out.println("Colorizing Boas");
//	for (int i = 0; i < boas.length; ++i) {
//	    colorizeBoa((BOA) boas[i], images, boaColors[i]);
//	}
        c.setResultImages(images);

//	System.out.println("Number of collected BOAs: " + boaCollection.size());
        return c;
    }

    /**Color the BOA in the specified BufferedImages with the specified color.
     * 
     * @param boa the BOA to color.
     * @param images the images the BOA may reside in.
     * @param boaColor the color to use for the BOA.
     */
    private void colorizeBoa(BOA boa, BufferedImage[] images, int boaColor) {

        for (Pixel p : boa) {
            BufferedImage bufferedImage = images[p.getZ()];
            int prevColor = bufferedImage.getRGB(p.getX(), p.getY());
            int newColor = boaColor;
            if (isGrayScale(prevColor) == false) {
                System.out.println("Combining colors from BOA!");
                newColor = combine(prevColor, boaColor);
            }
            bufferedImage.setRGB(p.getX(), p.getY(), newColor);
        }

    }

    /**Combine to colors into one.
     * The colors are encoded as a 4 byte ARGB with alpha set to max.
     * @param color1 the first color to use for the combination.
     * @param color2 the second color to use for the combination.
     * @return the two colors combined as one.
     */
    private int combine(int color1, int color2) {
        int red = (((color1 >> 16) & 0xff) + ((color2 >> 16) & 0xff)) / 2;
        int green = (((color1 >> 8) & 0xff) + ((color2 >> 8) & 0xff)) / 2;
        int blue = (((color1) & 0xff) + ((color2) & 0xff)) / 2;
        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

    /**Generate a specified number of random colors.
     * The colors are encoded as a 4 byte ARGB with alpha set to max.
     * @param size the number of colors to generate.
     * @return an array of the generated colors.
     */
    private int[] genColors(int size) {
        int[] colors = new int[size];
        for (int i = 0; i < colors.length; ++i) {
            int red = (int) (Math.random() * 255);
            int green = (int) (Math.random() * 255);
            int blue = (int) (Math.random() * 255);
            colors[i] = (255 << 24) | (red << 16) | (green << 8) | blue;
        }
        return colors;
    }

    private int randomColor() {
        int red = (int) (Math.random() * 255);
        int green = (int) (Math.random() * 255);
        int blue = (int) (Math.random() * 255);
        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

    /**Convert the Stack to an array of BufferedImage that we can manipulate.
     * @param stack the Stack to extract the images from.
     * @return an array of BufferedImage representing the Images in our Stack.
     */
    private BufferedImage[] getImagesFromStack(Stack stack) {
        BufferedImage[] images = new BufferedImage[stack.getDepth()];
        for (int i = 0; i < images.length; i++) {
            images[i] = stack.getImage(i).getImage();
        }
        return images;
    }

    /**Finds out if a color is gray scale or not.
     * The color should be encoded as a 4 byte ARGB.
     * @param rgb the color to check.
     * @return true if the color is gray scale and false otherwise.
     */
    private boolean isGrayScale(int rgb) {
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = rgb & 0xff;
        return red == green && red == blue;
    }
}
