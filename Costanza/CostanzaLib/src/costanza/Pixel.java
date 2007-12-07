package costanza;

/**Pixel is tripple of indices. It's used by Data classes
 */
public class Pixel {
    
    /** Pixel indices in x, y, z directions*/
    private int x, y, z;
    
    /**Create uninitialized Pixel*/
    public Pixel(){}
    
    /**Create Pixel with indices x, y and z
     *@param x int
     *@param y int
     *@param z int
     */
    public Pixel( int x,  int y, int z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**Copy constructor 
     * Create Pixel which is a copy of p*/
    public Pixel( Pixel p ) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }
    
    /**Get Pixel index in x direction.
     *@return int
     **/
    public int getX(){
        return x;
    }
    
    /**Gets Pixel index in y direction.
     *@return int
     **/
    public int getY(){
        return y;
    }
    
    /**Gets Pixel index in z direction.
     *@return int
     **/
    public int getZ(){
        return z;
    }
    
    /**Gets Pixel index in x direction.
     *@param x int
     **/
    public void setX( int x ){
        this.x = x;
    }
    
    /**Sets Pixel index in y direction.
     *@param y int
     **/
    public void setY( int y ){
        this.y = y;
    }
    
    /**Get Pixel index in z direction.
     *@param z int
     **/
    public void setZ( int z ){
        this.z = z;
    }
}
