package costanza;

import java.util.Collection;
import java.util.Vector;

/**CellIntensity is a Pixel, CellId and float intensity.*/
public class CellIntensity extends CellId{
    
    private Pixel pixel;
    private Vector<Float> intensities;
    
    /**Constructs uninitialized CellIntensity with id.
     *@param String id
     */
    public CellIntensity( int id ){
        super(id);
        intensities = new Vector<Float>();
    }
    
    /**Constructs CellIntensity with specified intensity and with id.
     *@param Sring id, float
     */
    public CellIntensity( int id, float intens ) {
        super(id);
        intensities = new Vector<Float>();
        intensities.add(intens);
    }
    /**Constructs CellIntensity with specified intensities and with id.
     *@param Sring id, Colllection<Float>
     */
    public CellIntensity( int id, Collection<Float> intens ) {
        super(id);
        intensities = new Vector<Float>(intens);
    }
    
    /**Adds/appends new intensity to the CellIntensity
     *@param float intensity
     */
    public void addIntensity( float intens ){
       intensities.add(intens);
    }
    
    /**Gets the associated Intensities.
     *@return Vector<float>
     */
    public Vector<Float> getIntensities(){
        return intensities;
    }
    
    /**Gets the associated Intensity at position i.
     *@param int position
     *@return float intensity
     */
    public float getIntensity( int pos ){
        return intensities.elementAt(pos);
    }  
}
