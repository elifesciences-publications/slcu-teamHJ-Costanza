package costanza;

import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class can be used to extract intensities in individual basin of attractions.
 *
 *
 * @see Processor
 */
public class IntensityFinder extends Processor {

    @Override
    public Case process(Case c, Options options) throws Exception {
        //System.out.println("IntensityFinder::process");

        // Check that there is an original stack in case
//        if (c.getOriginalStack() == null) {
//            throw new Exception("No original stack available in case");
//        }
        Stack stack;
        String stackTag = "";
        if(options.hasOption("OverrideStack")){
            stack = (Stack)options.getOptionValue("OverrideStack");
            stackTag = (String)options.getOptionValue("StackTag");
        }
        else{
            stack = c.getOriginalStack();
        }
        if(stack == null){
            throw new Exception("No stack available");
        }
        // Get the basin of attractors from data in case
        Vector<BOA> boa = new Vector<BOA>();
        Collection boaCollection = c.getCellData(DataId.BOAS);
        if (boaCollection != null && boaCollection.iterator().hasNext()) {
            //int count=0;
            Iterator i = boaCollection.iterator();
            while (i.hasNext()) {
                BOA boaTmp = (BOA) i.next();
                boa.add(boaTmp);
            //++count;
            }
        //System.out.println("Number of collected BOAs " + count);
        } else {//No boas found, nothing to do for this function
            return c;
        }
        // Extract and sve the intensities
        int numBoa = boa.size();
        Vector<Float> intensity = new Vector<Float>();
        Vector<Float> meanIntensity = new Vector<Float>();
        for (int i = 0; i < numBoa; ++i) {
            intensity.add(0.0f);
            meanIntensity.add(0.0f);
        }
        for (int i = 0; i < numBoa; ++i) {
            Vector<Pixel> pixels = new Vector<Pixel>(boa.get(i));
            int numPixel = pixels.size();
            for (int j = 0; j < numPixel; ++j) {
                int x = pixels.get(j).getX();
                int y = pixels.get(j).getY();
                int z = pixels.get(j).getZ();
                intensity.set(i, intensity.get(i) +
                        stack.getIntensity(x, y, z));
            }
        }
        for (int i = 0; i < numBoa; ++i) {
					int numPixel = (boa.get(i)).size();
					meanIntensity.set(i, intensity.get(i) / numPixel);
        }
        // Add the total and mean intensity
        Vector<CellIntensity> ciTmp = new Vector<CellIntensity>();
        for (int i = 0; i < numBoa; ++i) {
//            Vector<Float> tmp = new Vector<Float>();
//            tmp.add(intensity.get(i));
//            tmp.add(meanIntensity.get(i));

            CellIntensity intens = new CellIntensity();
            intens.addIntensity(stackTag + "total", intensity.get(i));
            intens.addIntensity(stackTag + "mean", meanIntensity.get(i));
            
            Cell cell = boa.get(i).getCell();
//            c.attachCellData(new CellIntensity(tmp), cell);
            c.attachCellData(intens, cell);
        }
        System.out.println("IntensityCounter:" + c.sizeOfData(DataId.INTENSITIES));

        return c;
    }
}
