package Costanza;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**Basin Of Atraction defines pixels that belong to the same atraction zone.
 */
public class CellNeighbors extends CellId{
    
    /**Collection of Strings identifiing Neighbors.*/
    private Set<String> neighbors;

    
    /**Constructs empty BOA with id.
     *@param String id
     */
    public CellNeighbors( String id ){
        super(id);
        neighbors = new TreeSet<String>();
    }
    
    /**Constructs BOA including id Strings in Collection c and with id.
     *@param String id, Collection<String> c
     */
    public CellNeighbors( String id, Collection<String> c ) {
        super(id);
        neighbors = new TreeSet<String>(c);
    }
    
    /**adds Neighbor to CellNeighbors.
     *@param String s neighbor id to be inserted
     */
    public void addNeighbor( String s ) {
        neighbors.add(s);
    }
    
    /**adds Neighbors from the Collection.
     *@param Collection<String> which content is to be inserted
     */
    public void addNeighbors( Collection<String> c ) {
        neighbors.addAll(c);
    }
    
    /**checks if the Sting s is the neighbor id.
     *@return boolean true for s being the neighbor id
     */
    public boolean hasNeighbor( String s ){
        return neighbors.contains( s );
    }
    
}
