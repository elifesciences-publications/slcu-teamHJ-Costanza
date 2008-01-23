package costanza;

/**
 * PeakMerger merges peaks if they are closer than some threshold.
 * @author pontus
 * @see Processor
 */
public class PeakMerger extends Processor {

    /**Implements the PeakMerger algorithm.
     * @param c the Case to work on. 
     * @param o the Option object to use.
     * @return a modified Case object
     * @see Processor
     */
    @Override
    public Case process(Case c, Options o) throws Exception {

	float R2 = ((Float) o.getOptionValue("radius")).floatValue();
        R2 *= R2;
        
	float[] scale2 = c.getStack().getScale();
        for( int i  = 0; i < scale2.length; ++i ){
            scale2[i] *= scale2[i]; 
        }
        
	CellCenter cent[] = new CellCenter[c.sizeOfData(DataId.CENTERS)];
        c.getCellData(DataId.CENTERS, cent);
	int numCent = cent.length;
        
        //status of the center -1 -processable, i merged to i, -2 - unmergable
        int stat[] = new int[numCent];
        for( int i  = 0; i < stat.length; ++i ){
            stat[i] = -1; 
        }
        

	for (int i = 0; i < numCent; ++i) {
	    
            //get cell to process
            int ind1 = i;
            if(stat[i] >= 0){
                ind1  = stat[i];
            }
            else if( stat[i] == -2 ){
                continue;
            }
            CellCenter c1 = cent[ind1];
            
            //marker for unmergable;
            boolean merged = false;
            
	    for (int j = i+1; j < numCent; ++j) {
                
                int ind2 = j;
                if(stat[j] >= 0){
                    ind2  = stat[j];
                }
                else if( stat[j] == -2 ){
                    continue;
                }
                CellCenter c2 = cent[ind2];
                
		merged = merged || testForMerging(c1, c2, scale2, R2, c);
	    }
            
            if(!merged){
                stat[i] = -2;
                stat[ind1] = -2;
            }
	}

        c.renumberCells();
	return c;

    }

    private boolean testForMerging(CellCenter c1, CellCenter c2, float[] scale2,
	    float R2, Case manip) throws Exception{

	boolean merged = false;
	if (! (c1==c2) ) {
	    if (getSqrDistance(c1, c2, scale2) < R2) {
		//System.out.println("merge " + cc1.getId() + " " + cc2.getId());
		manip.mergeAllData( c1.getCell(), c2.getCell() );

		merged = true;
	    }
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
	return (float)(
		(x1 - x2) * (x1 - x2) * scale2[0] +
		(y1 - y2) * (y1 - y2) * scale2[1] +
		(z1 - z2) * (z1 - z2) * scale2[2]);
    }
}
