package costanza;

import java.util.Vector;

/**
 * PeakMerger merges peaks if they are closer than some threshold.
 * @author pontus,pawel
 * @see Processor
 */
public class PeakMerger extends Processor {

    public static final String RADIUS_OPT = "radius";
    
    /**Implements the PeakMerger algorithm.
     * @param c the Case to work on. 
     * @param o the Option object to use.
     * @return a modified Case object
     * @see Processor
     */
    @Override
    public Case process(Case c, Options o) throws Exception {

        float R2 = 0.0f;
        if(o != null)
        {
            R2 = ((Float) o.getOptionValue(RADIUS_OPT)).floatValue();
        }
        else
            throw new Exception("No valid options send to PeakMerger.");
        
        R2 *= R2;

        float[] scale2 = c.getStack().getScale();
        for (int i = 0; i < scale2.length; ++i) {
            scale2[i] *= scale2[i];
        }

        CellCenter cent[] = new CellCenter[c.sizeOfData(DataId.CENTERS)];
        c.getCellData(DataId.CENTERS, cent);
        int numCent = cent.length;
        
        Vector<Integer> stat = new Vector<Integer>(numCent);
        
        for (int i = 0; i < numCent; ++i) {
            stat.add(i);
        }
        for (int i = 0; i < numCent; ++i) {
            int ind1 = stat.get(i);
            CellCenter c1 = cent[ind1];

            for (int j = i + 1; j < numCent; ++j) {

                int ind2 = stat.get(j);
                CellCenter c2 = cent[ind2];
                boolean merging = testForMerging(c1, c2, scale2, R2, c);
                if (merging) {

                    stat.set(ind2, ind1);
                }
            }
        }
        
        PixelFlag pf = (PixelFlag) c.getStackData(DataId.PIXEL_FLAG);
        pf.changeAll(stat);

        c.renumberCells(stat);
        pf.changeAll(stat);
        
        return c;
    }

    private boolean testForMerging(CellCenter c1, CellCenter c2, float[] scale2,
            float R2, Case manip) throws Exception {

        boolean merged = false;
        if (!(c1 == c2) && (getSqrDistance(c1, c2, scale2) < R2)) {
            //System.out.println("merge " + cc1.getId() + " " + cc2.getId());
            manip.mergeAllData(c1.getCell(), c2.getCell());

            merged = true;
        }
        return merged;
    }

    /**Calculates the distance between two CellCenters taking the
     * scale into account.
     * @param cc1 the first CellCenter.
     * @param cc2 the second CellCenter.
     * @param scale the scale to use for the different dimensions.
     * @return the distance between the two CellCenters.
     */
    private float getSqrDistance(CellCenter cc1, CellCenter cc2, float[] scale2) {
        float x1 = cc1.getX();
        float y1 = cc1.getY();
        float z1 = cc1.getZ();
        float x2 = cc2.getX();
        float y2 = cc2.getY();
        float z2 = cc2.getZ();
        //System.out.println("center 1:" + cc1.getId() + " " + x1 + " " + y1 + " " + z1);
        //System.out.println("center 2:" + cc2.getId() + " " + x2 + " " + y2 + " " + z2);
        return (float) ((x1 - x2) * (x1 - x2) * scale2[0] +
                (y1 - y2) * (y1 - y2) * scale2[1] +
                (z1 - z2) * (z1 - z2) * scale2[2]);
    }
}
