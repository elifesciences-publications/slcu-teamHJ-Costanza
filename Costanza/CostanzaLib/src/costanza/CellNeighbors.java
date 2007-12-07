package costanza;

import java.util.Collection;
import java.util.TreeSet;

/**Basin Of Atraction defines pixels that belong to the same atraction zone.
 */
public class CellNeighbors extends TreeSet<Integer> implements CellId{
    
    /**Id of the cell that data corresponds to*/
    int cellId;

    
    /**Constructs empty BOA with id.
     *@param id id
     */
    public CellNeighbors( int id ){
        cellId = id;
    }
    
    /**Constructs BOA including id Strings in Collection c and with id.
     *@param id int
     *@param c Collection<String>
     */
    public CellNeighbors( int id, Collection<Integer> c ) {
        super(c);
        cellId = id;
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
    public int getId(){
        return cellId;
    }
}
