/*
 * PeakMerger.java
 *
 * Created on December 1, 2007, 12:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Costanza;

import java.util.Collection;
import java.util.Iterator;

/**
 * PeakMerger merges peaks if they are closer than some threshold.
 *
 * @author pontus
 */
public class PeakMerger extends Processor{
    
    /** Creates a new instance of PeakMerger */
    public PeakMerger() {
    }
    
    /**
     * Implements the PeakMerger algorithm.
     * @param a Case and an Option
     * @returns a modified Case object
     * @see Processor
     */
    public Case process(Case c, Options o) throws Exception {
        Float R = (Float) o.getOptionValue("radius");
              float [] scale = c.getStack().getScale();
        Object[] centers = c.getData().getData(DataId.cellCenters).toArray();  
        for (int i =0; i < centers.length; ++i) {
            CellCenter cc1 = (CellCenter) centers[i];
            for (int j=i+1; j < centers.length; ++j) {
                CellCenter cc2 = (CellCenter) centers[j];
                if ( !cc1.toString().equals(cc2.toString())
                && getDistance(cc1,cc2, scale) < R.floatValue())
                    c.getManipulator().merge(DataId.cellCenters, cc1.getId(), cc2.getId(), cc1.getId());
            }
        }
        return c;
    }
    
    private void sendForMerging(CellCenter cc1, CellCenter cc2) {
     System.out.println("MERGE U FUCK");   
    }
    
    /**
     * Calculates the distance between two CellCenters taking the
     * scale int account.
     * @param two CellCenter and a float []
     * @return the distance between the CellCenters
     */
    private float getDistance(CellCenter cc1, CellCenter cc2, float [] scale) {
        float x1 = cc1.getX();
        float y1 = cc1.getY();
        float z1 = cc1.getZ();
        float x2 = cc2.getX();
        float y2 = cc2.getY();
        float z2 = cc2.getZ();
        //System.out.println("center 1:" + cc1.getId() + " " + x1 + " " + y1 + " " + z1);
        //System.out.println("center 2:" + cc2.getId() + " " + x2 + " " + y2 + " " + z2);
        return (float)Math.sqrt(
                (x1-x2)*(x1-x2)*scale[0]*scale[0] +
                (y1-y2)*(y1-y2)*scale[1]*scale[1] +
                (z1-z2)*(z1-z2)*scale[2]*scale[2] );
    }
}