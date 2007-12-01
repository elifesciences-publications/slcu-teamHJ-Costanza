package Costanza;

import Costanza.Pixel;

/**CellCenter is a Pixel plus cell id .
 */
public class CellCenter extends Pixel{
    
    private String id;
    
    public CellCenter( String id ){
        this.id = id;
    }
    
    public CellCenter( String id, int x, int y, int z ) {
        super(x, y, z);
        this.id = id;
    }
    
    public String getId(){
        return id;
    }
}
