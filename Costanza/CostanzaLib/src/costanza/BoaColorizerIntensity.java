package costanza;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**Implementation of a Processor that colors each BOA with a color defined by the value stored in Intensity.
 * It uses a mathlab color schema, i.e. from blue to green to yellow to red.
 * @author michael, henrik
 * @see Processor
 */
public class BoaColorizerIntensity extends Processor {

	@Override
    public Case process(Case c, Options options) throws Exception {
		// Get the basin of attractors from data in case
		Collection<BOA> boaCollection = (Collection<BOA>) c.getCellData(DataId.BOAS);
		if (boaCollection == null) {
			return c;
		}
		
		int[] boaColors = genColors(c,boaCollection.size());
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
	
	private int[] genColors(Case c, int size) {
		float[] value = new float[size];
		java.util.Set<Integer> cellIds = c.getCellIds();
		java.util.Iterator<Integer> iterator = cellIds.iterator();
		int count=0;
		float min=0.0f,max=0.0f;
		while (iterator.hasNext() && count<size) {
			Integer i = iterator.next();
			CellIntensity cellIntensity = (CellIntensity) c.getCellData(DataId.INTENSITIES, i);
			value[count] = cellIntensity.getIntensity(0);
			if (count==0 || value[count]<min) {
				min = value[count];
			}
			if (count==0 || value[count]>max) {
				max = value[count];
			}
			++count;
		}
		for (int i = 0; i < value.length; ++i) {
			value[i] = (value[i]-min)/(max-min);
		}
		int[] colors = new int[size];
		for (int i = 0; i < colors.length; ++i) {
			colors[i] = getColor(value[i]);
		}
		return colors;
	}
	
	private int getColor(float value) {
		
		float frac1 = 0.05f;
		float frac = (1.0f-2.0f*frac1)/3.0f;
		float red_=0.0f,green_=0.0f,blue_=0.0f;
		if( value<frac1 ) {
			red_ = green_ = 0.0f;
			blue_ = 0.5f*(1.0f+value/frac1);
		}
		else if( value>=(1.0f-frac1) ) {
			green_ = blue_ = 0.0f;
			red_ = 0.5f*( 1.0f+(1.0f-value)/frac1 );
		}
		else if( value>=frac1 && value<(frac+frac1)) {
			red_ = 0.0f;
			green_ = (value-frac1)/(frac);
			blue_ = 1.0f-green_;
		}
		else if( value>=(frac+frac1) && value<(frac1+2.0f*frac) ) {
			blue_ = 0.0f;
			green_ = 1.0f;
			red_ = (value-(frac+frac1))/frac;
		}
		else if( value>=(frac1+2.0f*frac) && value<(1.0f-frac1) ) {
			blue_ = 0.0f;
			green_ = (frac1+value-1)/(2.0f*frac1-frac);
			//g = ((frac1+2.0f*frac)-value)/frac;
			red_ = 1.0f;
		}
		int red = (int) (red_* 255);
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
}
