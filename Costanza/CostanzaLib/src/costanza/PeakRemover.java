/*
 * PeakRemover.java
 *
 * Created on December 1, 2007, 8:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package costanza;

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
        Float sizeThreshold = (Float) o.getOptionValue("sizeThreshold");
        Float intensityThreshold = (Float) o.getOptionValue("intensityThreshold");
        
        Object[] centers = c.getData().getData(DataId.cellCenters).toArray();
        System.out.println("centers " + centers.length );
        
        
        //Iterator it = centers.iterator();
        //while (it.hasNext()) {
        for (int i = 0; i < centers.length; ++i) {
            System.out.println("begin while " + centers.length);
            CellCenter cc = (CellCenter) centers[i];//(CellCenter) it.next();
            //System.out.println("created cc");
            int x = cc.getX();
            int y = cc.getY();
            int z = cc.getZ();
            //System.out.println("intensity: " + c.getStack().getIntensity(x,y,z));
            if (c.getStack().getIntensity(x,y,z) < intensityThreshold) {
                //System.out.println("before");
                c.getManipulator().removeAll(cc.getId());
                //System.out.println("after");
            }
            //System.out.println("after after");
        }
        //System.out.println("second loop");
        Object[] boas = c.getData().getData(DataId.cellBasinsOfAttraction).toArray();
        //it = boas.iterator();
        //System.out.println("boas " + boas.length);
        
        //while (it.hasNext()) {
        for (int i = 0; i < boas.length; ++i) {
            BOA boa= (BOA) boas[i];//it.next();
            float size =
                    boa.getPixels().size()*
                    (c.getStack().getXScale())*
                    (c.getStack().getYScale())*
                    (c.getStack().getZScale());
            //System.out.println("size: " + size);
            if (size < sizeThreshold.floatValue())
                c.getManipulator().removeAll(boa.getId());
        }
        return c;
    }
    
    public void removePeak(Object o) {
        System.out.println("REMOVE U FUCK");
        
    }
}