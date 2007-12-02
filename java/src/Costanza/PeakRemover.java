/*
 * PeakRemover.java
 *
 * Created on December 1, 2007, 8:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Costanza;

import java.util.Iterator;
import java.util.Collection;

/**
 * PeakRemover removes a peak if its smaller than some threshold size, 
 * or if has an intensity lower than some threshold.
 *
 * @author pontus
 */
public class PeakRemover extends Processor {
    
    /** Creates a new instance of PeakRemover */
    public PeakRemover() {
    }
    
    /**
     * Implements the PeakRemover algorithm.
     * @param a Case and an Option containing two 
     * thresholds for size and intensity.
     * @returns a modified Case object
     * @see Processor
     */
    public Case process(Case c, Options o) throws Exception {
        float sizeThreshold = (Float) o.getOptionValue("sizeThreshold");
        float intensityThreshold = (Float) o.getOptionValue("intensityThreshold");
        Collection centers = c.getData().getData(DataId.cellCenters);
        Iterator it = centers.iterator();
        while (it.hasNext()) {
            CellCenter cc = (CellCenter) it.next();
            int x = cc.getX();
            int y = cc.getY();
            int z = cc.getZ();
            //System.out.println("intensity: " + c.getStack().getIntensity(x,y,z));
            if (c.getStack().getIntensity(x,y,z) < intensityThreshold)
                removePeak(cc);
        }
        
        Collection boas = c.getData().getData(DataId.cellBasinsOfAtraction);
        it = boas.iterator();
        
        while (it.hasNext()) {
            BOA boa= (BOA) it.next();
            float size =
                    boa.getPixels().size()*
                    (c.getStack().getXScale())*
                    (c.getStack().getYScale())*
                    (c.getStack().getZScale());
            //System.out.println("size: " + size);
            if (size < sizeThreshold)
                removePeak(boa);
        }
        return c;
    }
    
    public void removePeak(Object o) {
        System.out.println("REMOVE U FUCK");
        
    }
}