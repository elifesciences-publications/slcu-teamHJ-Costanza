package costanza;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**Cell groups data for given cell*/
public class Cell implements Comparable<Cell>{

    /**cell id*/
    private int id;
    /**Map of data types to the data objects */
    private Map<DataId, CellData_t> cell_data;
    
    /**Constructs the cell of given id
     * @param i id of the new cell
     */
    public Cell(int i) {
        this.id = i;
        cell_data = new EnumMap<DataId, CellData_t>(DataId.class);
        cell_data.remove(DataId.BACKGROUND);
        clear();
    }

    /**Adds/replaces data in the cell
     * @param data
     */
    public void add(CellData_t data){
        cell_data.put( data.getDataId(), data );
    }
    
    /**Retrives data of given type from the cell
     * @param id of data type
     * @return cell data, null if no data present
     */
    public CellData_t get(DataId id){
        return cell_data.get( id );
    }
    
    /**Retrives all the data contained in the cell,
     * @return Collection of cell data. Collection may contain nulls if some of data types are not used in the cell.
     */
    public Collection< CellData_t >getData(){
        return cell_data.values();
    }
    
    /**Retrives all the data contained in the cell,
     * @return Set of map entries containing DataId and CellData_t
     */
    Set<Map.Entry<DataId, CellData_t> > entrySet(){
        return cell_data.entrySet();
    }
    
    /**Retrives data ids supported by the cell
     * @return Set of data ids
     */
    Set< DataId > keySet(){
        return cell_data.keySet();
    }
            
    /**Removes data of given type from the cell
     * @param id of the data to remove
     */
    public void remove(DataId id) {
        cell_data.put( id, null );
    }
    
    /**Clears all the data in the cell
     */
    public void clear(){
        cell_data.put(DataId.CENTERS, null);
        cell_data.put(DataId.BOAS, null);
        cell_data.put(DataId.NEIGHBORS, null);
        cell_data.put(DataId.INTENSITIES, null);
    }

    /* 
     * *Sets internal id of the cell. Use with caution as cells are maped to ids in Data too.
     * @param id
     */
    void seId( int id){
        this.id = id; 
    }
    
    public int compareTo(Cell src) {
        return ((Integer)id).compareTo( src.id );
    }
    
    @Override
    public boolean equals(Object src) {
        if (!(src instanceof Cell))
            return false;
        return ((Integer)id).equals( ((Cell)src).id );
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.id;
        return hash;
    }
    
    public int getCellId(){
       return id;
    }
}
