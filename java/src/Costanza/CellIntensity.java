package Costanza;

/**CellIntensity is a Pixel, CellId and float intensity.*/
public class CellIntensity extends CellId{
    
    private Pixel pixel;
    private float intensity;
    
    /**Constructs uninitialized CellIntensity with id.
     *@param String id
     */
    public CellIntensity( String id ){
        super(id);
    }
    
    /**Constructs CellIntensity with specified intensity and with id.
     *@param Sring id, float
     */
    public CellIntensity( String id, float intens ) {
        super(id);
        intensity = intens;
    }
    
    /**Gets the associated Intensity.
     *@return float
     */
    public float getIntensity(){
        return intensity;
    }
}
