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

    @Override
    public Case process(Case c, Options options) throws Exception {
        // Get the basin of attractors from data in case
        Collection<BOA> boaCollection = (Collection<BOA>) c.getCellData(DataId.BOAS);
        if (boaCollection == null) {
            return c;
        }
        int[] boaColors = genColors(boaCollection.size());
        BufferedImage[] images = getImagesFromStack(c.getStack());
        Object[] boas = boaCollection.toArray();
        if (boaColors.length != boas.length) {
            throw new Exception("Lengths differ!");
        }
        for (int i = 0; i < boas.length; ++i) {
            colorizeBoa((BOA)boas[i], images, boaColors[i]);
        }
        c.setResultImages(images);

        System.out.println("Number of collected BOAs: " + boaCollection.size());
        return c;
    }

    private void colorizeBoa(BOA boa, BufferedImage[] images, int boaColor) {
        for (int j = 0; j < boa.size(); j++) {
            Pixel p = boa.get(j);
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

    private int combine(int color1, int color2) {
        int red = (((color1 >> 16) & 0xff) + ((color2 >> 16) & 0xff)) / 2;
        int green = (((color1 >> 8) & 0xff) + ((color2 >> 8) & 0xff)) / 2;
        int blue = (((color1) & 0xff) + ((color2) & 0xff)) / 2;
        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

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
}
