package Costanza;
import java.util.Vector;
import java.util.Collection;
/**
 * This class can be used to extract intensities in individual basin of attractions.
 *
 *
 * @see Processor
 */
public class IntensityFinder extends Processor {
    
    public Case process(Case c, Options options) throws Exception {
        System.out.println("IntensityFinder::process");
        float radius = (Float) (options.getOptionValue("radius"));
        
        // Check that there is an original stack in case
        if( c.getOriginalStack() == null) {
            throw new Exception("No original stack available in case");
        }
				// Get the basin of attractors from data in case
				Vector<BOA> boa = new Vector<BOA>();
 				Collection boaCollection = c.getData().getData(DataId.cellBasinsOfAttraction);				
 				if (boaCollection!=null && boaCollection.iterator().hasNext()) {
						while (boaCollection.iterator().hasNext()) {
								BOA boaTmp = (BOA) boaCollection.iterator().next();
								boa.add( boaTmp );
								//boa.add( new Vector<Pixel>(boaTmp.getPixels()) );
						}
				}
				else {//No boas found, nothing to do for this function
						return c;
				}											

				// Extract and sve the intensities
				int numBoa = boa.size();
				Vector<Float> intensity = new Vector<Float>();
				Vector<Float> meanIntensity = new Vector<Float>();
				for (int i=0; i<numBoa; ++i) {
						intensity.add(0.0f);
						meanIntensity.add(0.0f);
        }
				
 				for (int i=0; i<numBoa; ++i) {						
						Vector<Pixel> pixels = new Vector<Pixel>(boa.get(i).getPixels());
 						int numPixel = pixels.size();
 						for (int j=0; j<numPixel; ++j) {
								int x = pixels.get(i).getX();
								int y = pixels.get(i).getY();
								int z = pixels.get(i).getZ();
								intensity.set(i,intensity.get(i)+
															c.getOriginalStack().getIntensity(x,y,z));
						}
				}
				for (int i=0; i<numBoa; ++i) {
						int numPixel = 1;//boa.get(i).size();
						meanIntensity.set(i,intensity.get(i)/numPixel);
				}
				
				// Add the total and mean intensity
				Vector<CellIntensity> ciTmp = new Vector<CellIntensity>();
				for (int i=0; i<numBoa; ++i) {
						Vector<Float> tmp = new Vector<Float>();
						tmp.add(intensity.get(i));
						tmp.add(meanIntensity.get(i));
						ciTmp.add(new CellIntensity(boa.get(i).getId(),tmp));
				}
				c.getData().attachDataCollection(DataId.cellIntensity,ciTmp);
				System.out.println("IntensityCounter:" + c.getData().sizeOfData(DataId.cellIntensity));

        return c;
    }
}
