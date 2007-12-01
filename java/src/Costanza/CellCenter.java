package Costanza;

import Costanza.Pixel;

/**CellCenter is a Pixel plus cell id.
 */
public class CellCenter extends Pixel{
    
    /**Id of the center*/
    private String id;
    
    /**Constructs uninitialized CellCenter with id.
     *@param String id
     */
    public CellCenter( String id ){
        this.id = id;
    }
    
    /**Constructs CellCenter at indices x, y, z and with id.
     *@param Sring id, int x, int y, int z
     */
    public CellCenter( String id, int x, int y, int z ) {
        super(x, y, z);
        this.id = id;
    }
    
    /**retrives id of the CellCenter.
     *@return String
     */
    public String getId(){
        return id;
    }
}
