package Costanza;
import java.util.Vector;
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
				// ...
				Vector<BOA> boa = new Vector<BOA>();


				int numBoa = boa.size();
				//Save the intensities
				Vector<Float> intensity = new Vector<Float>();
				Vector<Float> meanIntensity = new Vector<Float>();
				intensity.setSize(0);
				meanIntensity.setSize(0);

				for (int i=0; i<numBoa; ++i) {
						intensity.add(0.0f);
						meanIntensity.add(0.0f);
        }

// 				for (int i=0; i<numBoa; ++i) {						
// 						int numPixel = boa.get(i).numPixel();
// 						for (int j=0; j<numPixel; ++j) {
// 								int x = boa.get(i).getPixel(j).getX();
// 								int y = boa.get(i).getPixel(j).getY();
// 								int z = boa.get(i).getPixel(j).getZ();
// 								intensity.set(i,intensity.get(i)+
// 															c.getOriginalStack().Intensity(x,y,z));
// 						}
// 				}
				for (int i=0; i<numBoa; ++i) {
						int numPixel = 1;//boa.get(i).size();
						meanIntensity.set(i,intensity.get(i)/numPixel);
				}

				/** @todo Add the extracted intesities to the data structure
				 */
        return c;
    }
}
