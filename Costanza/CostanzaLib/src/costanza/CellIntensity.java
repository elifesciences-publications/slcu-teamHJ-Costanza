package costanza;

import java.util.Collection;
import java.util.Vector;

/**CellIntensity is a Pixel, CellId and float intensity.*/
public class CellIntensity extends Vector<Float> implements CellId{
    
    /**Id of the cell that data corresponds to*/
    int cellId;
    
    /**Constructs uninitialized CellIntensity with id.
     *@param int id
     */
    public CellIntensity( int id ){
        cellId = id;
    }
    
    /**Constructs CellIntensity with specified intensity and with id.
     *@param int id, float
     */
    public CellIntensity( int id, float intens ) {
        super();
        add(intens);
        cellId = id;
       
    }
    /**Constructs CellIntensity with specified intensities and with id.
     *@param int id, Colllection<Float>
     */
    public CellIntensity( int id, Collection<Float> intens ) {
        super(intens);
        cellId = id;
    }
    
    /**Adds/appends new intensity to the CellIntensity
     *@param float intensity
     */
    public void addIntensity( float intens ){
       add(intens);
    }
    
        /**Gets the id of the data's cell.
     *@return int
     */
    public int getId(){
        return cellId;
    }
    
//    /**Gets the associated Intensities.
//     *@return Vector<float>
//     */
//    public Vector<Float> getIntensities(){
//        return intensities;
//    }
    
//    /**Gets the associated Intensity at position i.
//     *@param int position
//     *@return float intensity
//     */
//    public float getIntensity( int pos ){
//        return intensities.elementAt(pos);
//    }  
}
