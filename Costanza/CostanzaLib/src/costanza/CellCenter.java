package costanza;

//import CellId;
//import Costanza.Pixel;

/**CellCenter is a Pixel plus cell id.
 */
public class CellCenter extends Pixel implements CellId{
    
    /**Id of the cell that data corresponds to*/
    int cellId;
    
    /**Constructs uninitialized CellCenter with id.
     *@param int id
     */
    public CellCenter( int id ){
        cellId = id;
    }
    
    /**Constructs CellCenter at indices x, y, z and with id.
     *@param int id, int x, int y, int z
     */
    public CellCenter( int id, int x, int y, int z ) {
        super(x, y, z);
        cellId = id;
    }
    
     /**Constructs CellCenter at a Pixel p and with id.
     *@param int id, Pixel p
     */
    public CellCenter( int id, Pixel p ) {
        super(p);
        cellId = id;
    }
    
    /**Gets the id of the data's cell.
     *@return int
     */
    public int getId(){
        return cellId;
    }
    
}
