package costanza;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**Implementation of a Processor that colors each BOA with a color defined by the value stored in Intensity.
 * It uses a mathlab color schema, i.e. from blue to green to yellow to red.
 * @author michael, henrik
 * @see Processor
 */
public class BoaColorizerIntensity extends Processor {

    @Override
    @SuppressWarnings("unchecked")
    public Case process(Case c, Options options) throws Exception {
        // Get the basin of attractors from data in case
//        Collection<BOA> boaCollection = (Collection<BOA>) c.getCellData(DataId.BOAS);
//        if (boaCollection == null) {
//            return c;
//        }

        if (options != null && options.hasOption("OverrideStack")) {
            stack = (Stack) options.getOptionValue("OverrideStack");

        } else {
            stack = c.getOriginalStack();
        }
        if (stack == null) {
            throw new Exception("No stack available");
        }

//        System.out.println("Processing stack: " + stack.getId());
//        System.out.println(c.getIntensityTagSet());
        int[] boaColors = genColors(c, c.sizeOfCells());
        //System.out.println("Colors generated");
        BufferedImage[] images = getImagesFromStack(c.getOriginalStack());
        Object[] cells = c.getCells().toArray();
        if (boaColors.length != cells.length) {
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
                    short flag = pf.get_flag(ix, iy, iz);
                    if (flag != PixelFlag.BACKGROUND_FLAG) {
                        int color;
                        if (flag >= curSize) {
                            color = randomColor();
                        } else {
                            color = boaColors[flag];
                        }

                        bufferedImage.setRGB(ix, iy, color);

                    }
                }
            }
        }

//        int[] boaColors = genColors(c, boaCollection.size());
//        BufferedImage[] images = getImagesFromStack(stack);
//        Object[] boas = boaCollection.toArray();
////        System.out.println("BOA length: " + boas.length);
//        
//        if (boaColors.length != boas.length) {
//            throw new Exception("Lengths differ!");
//        }
//        for (int i = 0; i < boas.length; ++i) {
//            colorizeBoa((BOA) boas[i], images, boaColors[i]);
//        }
//        c.setResultImages(images);
//
//        System.out.println("Number of collected BOAs: " + boaCollection.size());
        return c;
    }

    private void colorizeBoa(BOA boa, BufferedImage[] images, int boaColor) {
        for (Pixel p : boa) {
            BufferedImage bufferedImage = images[p.getZ()];
            bufferedImage.setRGB(p.getX(), p.getY(), boaColor);
        }
    }

    private int[] genColors(Case c, int size) throws Exception {

        String stackTag = Integer.toString(stack.getId()) + "mean";

        float[] value = new float[size];
        java.util.Set<Integer> cellIds = c.getCellIds();
        java.util.Iterator<Integer> iterator = cellIds.iterator();
        int count = 0;
        float min = 0.0f;
        float max = 0.0f;

        int index = c.getIntensityIndex(stackTag);
        //System.out.println("Intensity for stack: " + stackTag + " = " + index );
        while (iterator.hasNext() && count < size) {
            Integer i = iterator.next();
            CellIntensity cellIntensity = (CellIntensity) c.getCellData(DataId.INTENSITIES, i);
            //System.out.println("Cell: " + i + "Intensity size = " + cellIntensity.size() );
            value[count] = cellIntensity.getIntensity(index);
            if (count == 0 || value[count] < min) {
                min = value[count];
            }
            if (count == 0 || value[count] > max) {
                max = value[count];
            }
            ++count;
        }
        //System.out.println("Size: " + size);
        //System.out.println("Count: " + size);
        //System.out.println("Min: " + min);
        //System.out.println("Max: " + max);
        for (int i = 0; i < value.length; ++i) {
            value[i] = (value[i] - min) / (max - min);
        }
        int[] colors = new int[size];
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = getColor(value[i]);
        }
        return colors;
    }

    private int randomColor() {
        int red = (int) (Math.random() * 255);
        int green = (int) (Math.random() * 255);
        int blue = (int) (Math.random() * 255);
        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

    private int getColor(float value) {

        float frac1 = 0.05f;
        float frac = (1.0f - 2.0f * frac1) / 3.0f;
        float red_ = 0.0f, green_ = 0.0f, blue_ = 0.0f;
        if (value < frac1) {
            red_ = green_ = 0.0f;
            blue_ = 0.5f * (1.0f + value / frac1);
        } else if (value >= (1.0f - frac1)) {
            green_ = blue_ = 0.0f;
            red_ = 0.5f * (1.0f + (1.0f - value) / frac1);
        } else if (value >= frac1 && value < (frac + frac1)) {
            red_ = 0.0f;
            green_ = (value - frac1) / (frac);
            blue_ = 1.0f - green_;
        } else if (value >= (frac + frac1) && value < (frac1 + 2.0f * frac)) {
            blue_ = 0.0f;
            green_ = 1.0f;
            red_ = (value - (frac + frac1)) / frac;
        } else if (value >= (frac1 + 2.0f * frac) && value < (1.0f - frac1)) {
            blue_ = 0.0f;
            green_ = (frac1 + value - 1) / (2.0f * frac1 - frac);
            //g = ((frac1+2.0f*frac)-value)/frac;
            red_ = 1.0f;
        }
        //System.out.println(value + " " + red_ + " " + green_ + " " + blue_);
        int red = (int) (red_ * 255);
        int green = (int) (green_ * 255);
        int blue = (int) (blue_ * 255);
        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

    private BufferedImage[] getImagesFromStack(Stack stack) {
        BufferedImage[] images = new BufferedImage[stack.getDepth()];
        for (int i = 0; i < images.length; i++) {
            images[i] = stack.getImage(i).getImage();
        }
        return images;
    }

    private boolean isGrayScale(int rgb) {
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = rgb & 0xff;
        return red == green && red == blue;
    }
    private Stack stack;
}
