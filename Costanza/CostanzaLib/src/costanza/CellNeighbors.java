package costanza;

import java.util.Collection;
import java.util.TreeSet;

/**Basin Of Atraction defines pixels that belong to the same atraction zone.
 */
public class CellNeighbors extends TreeSet<Integer> implements CellData_t{
    
    /**Id of the cell that data corresponds to*/
    //int cellId;

    Cell cell;
    
    /**Constructs empty BOA with id.
     *@param id id
     */
    public CellNeighbors(){
        //cellId = id;
    }
    
    /**Constructs BOA including id Strings in Collection c and with id.
     *@param id int
     *@param c Collection<String>
     */
    public CellNeighbors( Collection<Integer> c ) {
        super(c);
        //cellId = id;
    }
    
    /**adds Neighbor to CellNeighbors.
     *@param n int neighbor id to be inserted
     */
    public void addNeighbor( int n ) {
        add(n);
    }
    
    /**adds Neighbors from the Collection.
     *@param c Collection<Int> which content is to be inserted
     */
    public void addNeighbors( Collection<Integer> c ) {
        addAll(c);
    }
    
    /**checks if the int s is the neighbor id.
     *@return boolean true for s being the neighbor id
     */
    public boolean hasNeighbor( int s ){
        return contains( s );
    }
    
   /**Gets the id of the data's cell.
     *@return int
     */
    public int getCellId(){
        return cell.getCellId();
    }

    /**Gets the id of the data.
     *@return DataId
     */
    public DataId getDataId() {
        return DataId.NEIGHBORS;
    }
    
    /**Sets the cell which owns the data.
     *@param Cell cell
     */
    public void setCell(Cell cell) {
        this.cell = cell;
    }
    
    /**Gets the the data's cell.
     *@return Cell
     */
    public Cell getCell() {
        return cell;
    }
}
