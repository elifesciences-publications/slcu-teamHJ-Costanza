package costanza;

import java.awt.image.BufferedImage;

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

        int[] boaColors = genColors(c.sizeOfCells());
        BufferedImage[] images = getImagesFromStack(c.getOriginalStack());
        if (boaColors.length != c.sizeOfCells()) {
            throw new Exception("Lengths differ!");
        }

        int xDim = images[0].getWidth();
        int yDim = images[0].getHeight();
        int zDim = images.length;
        int curSize = boaColors.length;
        PixelFlag pf = (PixelFlag) c.getStackData(DataId.PIXEL_FLAG);

        for (int iz = 0; iz < zDim; ++iz) {
            BufferedImage bufferedImage = images[iz];
            for (int iy = 0; iy < yDim; ++iy) {
                for (int ix = 0; ix < xDim; ++ix) {
                    int flag = pf.getFlag(ix, iy, iz);
                    if (flag != PixelFlag.BACKGROUND_FLAG) {
                        if (flag < 0) {
                            throw new Exception("Negative flag in PixelFlag!");
                        }
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
        c.setResultImages(images);
        return c;
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
