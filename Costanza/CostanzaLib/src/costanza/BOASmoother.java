package costanza;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**This is experimental code.
 * This proceessor is supposed to give BOAs more cell like shape. It removes and 
 * adds pixels to boa based on number of nearest neighbors of given pixel belonging 
 * to BOA. If this number is below lowerLimit pixel is removed. If it is above upper 
 * limit pixel is added. Doesn't check if added pixels belong to other BOA.  
 * @author pawel
 */
public class BOASmoother extends Processor {

    @Override
    public Case process(Case c, Options options) throws Exception {

/*
        final int upperLimit = ((Integer) options.getOptionValue("upperNeighborLimit")).intValue();
        final int lowerLimit = ((Integer) options.getOptionValue("lowerNeighborLimit")).intValue();
       
        BOA boas[] = new BOA[c.sizeOfCells()];
        boas = c.getCellData(DataId.BOAS, boas);

        //define neighbours of pixel(0,0,0)
        Vector<Pixel> neigh = new Vector<Pixel>(26);
        for (int zI = -1; zI <= 1; ++zI) {
            for (int yI = -1; yI <= 1; ++yI) {
                for (int xI = -1; xI <= 1; ++xI) {
                    if (xI != 0 || yI != 0 || zI != 0) {
                        neigh.add(new Pixel(xI, yI, zI));
                    }
                }
            }
        }
        //System.out.println("nearset neighbours defined: " + neigh.size());
        //smooth each boa
        Pixel testP = new Pixel(0, 0, 0);
        for (BOA boa : boas) {
            int remCount = 0, addCount = 0;
            Set<Pixel> sour = new TreeSet<Pixel>();
            Iterator<Pixel> iter = boa.iterator();
            while(iter.hasNext()){
                Pixel p = iter.next();
                int count = 0;
                for (Pixel neighP : neigh) {
                    testP.setXYZ(p.getX() + neighP.getX(), p.getY() + neighP.getY(), p.getZ() + neighP.getZ());
                    if (boa.hasPixel(testP)) {
                        ++count;
                    } else {
                        sour.add(new Pixel(testP));
                    }
                }
                if (count < lowerLimit) {
                    iter.remove();
                    ++remCount;
                }
            }
            
            for (Pixel s : sour) {
                int count = 0;
                for (Pixel neighP : neigh) {
                    testP.setXYZ(s.getX() + neighP.getX(), s.getY() + neighP.getY(), s.getZ() + neighP.getZ());
                    if (boa.hasPixel(testP)) {
                        ++count;
                    }
                }
                if (count > upperLimit) {
                    boa.add(s);
                    ++addCount;
                }
            }
            //System.out.println("BOA " + boa.hashCode() + " removed: " + remCount + " added: " + addCount);
        }
         */
        return c;
    }
}