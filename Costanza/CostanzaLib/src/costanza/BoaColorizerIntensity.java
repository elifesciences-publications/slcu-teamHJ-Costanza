package costanza;

import java.awt.image.BufferedImage;

/**Implementation of a Processor that colors each BOA with a color defined by the value stored in Intensity.
 * It uses a mathlab color schema, i.e. from blue to green to yellow to red.
 * @author michael, henrik
 * @see Processor
 */
public class BoaColorizerIntensity extends Processor {
public static final String STACK_OPT = "OverrideStack";
public static final String NORMALIZE_OPT = "NormalizeIntensities";
    @Override
    @SuppressWarnings("unchecked")
    public Case process(Case c, Options options) throws Exception {

//        System.out.println("BoaColorizerIntensity::process");
        if (options != null && options.hasOption(STACK_OPT)) {
            stack = (Stack) options.getOptionValue(STACK_OPT);
        } else {
            stack = c.getOriginalStack();
        }
        if (stack == null) {
            throw new Exception("No stack available");
        }
        boolean normalize = true;
        if (options != null && options.hasOption(NORMALIZE_OPT)) {
            normalize = (Boolean)options.getOptionValue(NORMALIZE_OPT);
        }

        int[] boaColors = genColors(c, c.sizeOfCells(), normalize);

        BufferedImage[] images = getImagesFromStack(stack);
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
                        if (flag < 0)
                            throw new Exception("Negative flag in PixelFlag!");
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
        c.setResultImages(images);
        return c;
    }

    private int[] genColors(Case c, int size, boolean normalize) throws Exception {

        String stackTag = Integer.toString(stack.getId()) + "mean";

        float[] value = new float[size];
        java.util.Set<Integer> cellIds = c.getCellIds();
        java.util.Iterator<Integer> iterator = cellIds.iterator();
        int count = 0;
        int index = c.getIntensityIndex(stackTag);
        while (iterator.hasNext() && count < size) {
            Integer i = iterator.next();
            CellIntensity cellIntensity = (CellIntensity) c.getCellData(DataId.INTENSITIES, i);
            value[count] = cellIntensity.getIntensity(index);
            ++count;
        }
        float min = 0.0f;
        float max = 0.0f;
        if (normalize) {
            for (int i = 0; i < value.length; ++i) {
                if (i == 0 || value[i] < min) {
                    min = value[i];
                }
                if (i == 0 || value[i] > max) {
                    max = value[i];
                }
            }
        } else {
            max = 1.0f;
        }
        for (int i = 0; i < value.length; ++i) {
            double diff = max - min;
            if(diff != 0.0)
                value[i] = (value[i] - min) / (max - min);
            else
                value[i] = 1.0f;
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
            red_ = 1.0f;
        }
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
