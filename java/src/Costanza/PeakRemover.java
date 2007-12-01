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
import java.util.Vector;

/**
 *
 * @author pontus
 */
public class PeakRemover extends Processor {
    
    /** Creates a new instance of PeakRemover */
    public PeakRemover() {
    }
    
    /**
     * Implements the PeakRemover algorithm.
     * @param a Case and an Option
     * @returns a modified Case object
     * @see Processor
     */
    public Case process(Case c, Options o) throws Exception {
        float sizeThreshold = (Float) o.getOptionValue("size");
        float intensityThreshold = (Float) o.getOptionValue("intensity");
        Vector centers = (Vector) (c.getData().getData(DataId.cellCenters));
        
        Iterator it = centers.iterator();
        while (it.hasNext()) {
            CellCenter cc = (CellCenter) it.next();
            int x = cc.getX();
            int y = cc.getY();
            int z = cc.getZ();
            if (c.getStack().getIntensity(x,y,z) < intensityThreshold)
                removePeak(cc);
        }
        
        Vector boas = (Vector) (c.getData().getData(DataId.cellBasinsOfAtraction));
        it = boas.iterator();
        
        while (it.hasNext()) {
            BOA boa= (BOA) it.next();
            float size =
                    boa.getPixels().size()*
                    (c.getStack().getXScale())*
                    (c.getStack().getYScale())*
                    (c.getStack().getZScale());
            if (size < sizeThreshold)
                removePeak(boa);
        }
        return c;
    }
    
    public void removePeak(Object o) {
        
    }
}