package Costanza;

//import CellId;
//import Costanza.Pixel;

/**CellCenter is a Pixel plus cell id.
 */
public class CellCenter extends CellId{
    
    private Pixel pixel;
    
    /**Constructs uninitialized CellCenter with id.
     *@param String id
     */
    public CellCenter( String id ){
        super(id);
        pixel = new Pixel();
    }
    
    /**Constructs CellCenter at indices x, y, z and with id.
     *@param Sring id, int x, int y, int z
     */
    public CellCenter( String id, int x, int y, int z ) {
        super(id);
        pixel = new Pixel(x, y, z);
    }
    
    /**Gets the Pixel defining the center.
     *@return Pixel
     */
    public Pixel getPixel(){
        return pixel;
    }
    
    /**Get Pixel index in x direction.
     *@return int
     **/
    public int getX(){
        return pixel.getX();
    }
    
    /**Gets Pixel index in y direction.
     *@return int
     **/
    public int getY(){
        return pixel.getY();
    }
    
    /**Gets Pixel index in z direction.
     *@return int
     **/
    public int getZ(){
        return pixel.getZ();
    }
    
    /**Gets Pixel index in x direction.
     *@param int
     **/
    public void setX( int x ){
        pixel.setX(x);
    }
    
    /**Sets Pixel index in y direction.
     *@param int
     **/
    public void setY( int y ){
        pixel.setY(y);
    }
    
    /**Get Pixel index in z direction.
     *@param int
     **/
    public void setZ( int z ){
        pixel.setZ(z);
    }
}
