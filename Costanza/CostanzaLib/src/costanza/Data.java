package costanza;

import java.util.Collection;
import java.util.Vector;
import java.util.Map;
import java.util.Set;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.TreeMap;

/**Data is a container for different types of data used by Processor.
 */
public class Data {

    /**Maps dataId to a Vector of Objects which represent data*/
    private Map<DataId, Data_t> stackDataMap;
    private Map<DataId, Collection<? extends CellData_t>> cellDataMap;
    private Map<Integer, Cell> cells;

    public Data() {

        cells = new TreeMap<Integer, Cell>();
        stackDataMap = new EnumMap<DataId, Data_t>(DataId.class);
        stackDataMap.put(DataId.BACKGROUND, null);
        cellDataMap = new EnumMap<DataId, Collection<? extends CellData_t>>(DataId.class);
        cellDataMap.put(DataId.CENTERS, new Vector<CellCenter>());
        cellDataMap.put(DataId.BOAS, new Vector<BOA>());
        cellDataMap.put(DataId.NEIGHBORS, new Vector<CellNeighbors>());
        cellDataMap.put(DataId.INTENSITIES, new Vector<CellIntensity>());
    }

    /**Returns Cell with given id
     * @param cellId int
     * @return Cell
     */
    public Cell getCell( int cellId ){
        return cells.get( cellId );
    }
    
    /**Gives the size of cells 
     * @return number of cells contained in the data
     */
    public int sizeOfCells(){
        return cells.size();
    }
    
    /**Deprecated method. Avoid using it. It might be not supported in the next version.
     * Gets Set of keys of available data types for given DataGroup
     * @param dg DataGroup
     * @return Set<DataId>
     * @throws java.lang.Exception
     */
    public Set<DataId> getDataKeys(DataGroup dg) throws Exception {
        switch (dg) {
            case CELL:
                return cellDataMap.keySet();
            case STACK:
                return stackDataMap.keySet();
            default:
                throw new Exception("Unsupported DataGroup.");
        }
    }

    /**Deprecated method. Avoid using it. It might be not supported in the next version.
     * Gives the size of the data of given type
     * @param id DataId
     * @return int
     */
    public int sizeOfData(DataId id) {
        if (cellDataMap.containsKey(id)) {
            return getCellData(id).size();
        } else if (stackDataMap.containsKey(id)) {
            return 1;
        }
        return 0;
    }

    /**Deprecated method. Avoid using it. It might be not supported in the next version.
     * Retrives cell data Collection associated with given dataId
     * @param id DataId
     * @return Collection<? extends CellData_t >, null if no data of given type found
     */
    public Collection<? extends CellData_t > getCellData(DataId id) {
        return cellDataMap.get(id);
    }
    
    /**Retrives cell data of given Cell associated with given dataId
     * @param dId DataId
     * @param cId int
     * @return CellData_t
     */
    public CellData_t  getCellData(DataId dId, int cId) {
        return getCellData( dId, cells.get(cId) );
    }
    
    /**Retrives cell data of given Cell associated with given dataId
     * @param dId DataId
     * @param c Cell
     * @return CellData_t
     */
    public CellData_t  getCellData(DataId dId, Cell c) {
        if(c == null)
            return null;
        else
            return c.get(dId);
    }
    
    /**Retrives a set of cell ids stored in data. 
     * Set is tied to cell data, so changes in the set are reflected in store cells.
     * @return Set of cell ids
     */
    public Set<Integer> getCellIds(){
        return cells.keySet();
    }
    
    /**Retrives a collection of Cells stored in data. 
     * Collection is tied to the data, so changes in either one are reflected in the other.
     * @return Collection of Cell objects
     */
    public Collection<Cell> getCells(){
        return cells.values();
    }
    
    /**Retrives stack data of given dataId
     * @param id DataId
     * @return Data_t
     */
    public Data_t getStackData( DataId id ) {
        return stackDataMap.get(id);
    }

    /**Attaches stack data
     * @param data
     * @throws java.lang.Exception
     */
    public <T extends Data_t> void attachStackData( T data ) throws Exception {
        DataId id = data.getDataId();
        if (getDataKeys(DataGroup.STACK).contains(data.getDataId())) {
            stackDataMap.put(id, data);
        }
    }

    /**Attaches cell data to cell with given id
     * @param data
     * @param cellId Integer
     */
    public <T extends CellData_t> void attachCellData(T data, Integer cellId) {

        Cell cell = cells.get(cellId);
        if (cell == null) {
            cell = new Cell(cellId);
        }
        
        attachCellData( data, cell );
    }

    /**Attaches data to given cell
     * @param data
     * @param cell Cell
     */
    @SuppressWarnings("unchecked")
    public <T extends CellData_t> void attachCellData(T data, Cell cell) {

        cell.add( data );
        data.setCell( cell );
        
        Collection<T> dColl = (Collection<T>) cellDataMap.get(data.getDataId());
        dColl.add( data );
        
        cells.put(cell.getCellId(), cell);

    }
    
    /**Removes data from data set
     * @param d Data_t
     */
    public void removeData(Data_t d) {
        DataId id = d.getDataId();
        
        if (id.getGroup() == DataGroup.CELL) {
            Collection<? extends Data_t> dColl = cellDataMap.get(id);
            if(dColl != null)
                dColl.remove(d);
            
            Cell cell = ((CellData_t)d).getCell();
            if(cell != null)
                cell.remove(id);
        } 
        else if (id.getGroup() == DataGroup.STACK) {
            stackDataMap.put(id, null);
        }
    }
    
    /**Removes data of given type form the cell
     * @param dId DataId
     * @param c Cell
     */
    public void removeCellData(DataId dId, Cell c) {
        if(c == null)
            return;
        Data_t d = c.get(dId);
        cellDataMap.get(dId).remove(d);
	c.remove(dId);
    }
    
    /**Removes data of dgiven type from the cell with given id
     * @param dId DataId
     * @param cId int
     */
    public void removeCellData(DataId dId, int cId) {
        Cell cell = cells.get(cId);
        removeCellData( dId, cell );
    }
    
    /**Removes all the data from given cell
     * @param c Cell
     */
    public void removeAllCellData(Cell c){
        if(c == null)
            return;
        Iterator< Map.Entry<DataId, CellData_t> > iter = c.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<DataId, CellData_t> pairs = (Map.Entry<DataId, CellData_t>) iter.next();
            CellData_t data = pairs.getValue();
            if( data != null )
                cellDataMap.get(pairs.getKey()).remove(data);
        }
        
        c.clear();
    }
    
    /** all the data from the cell with given id
     * @param cId int
     */
    public void removeAllCellData(int cId){
        Cell cell = cells.get(cId);
        removeAllCellData(cell);
    }
    
    /**Removes cell and all its data
     *@param c cell to remove
     */
    public void removeCell(Cell c) {

        if(c == null)
            return;
        Iterator< Map.Entry<DataId, CellData_t> > iter = c.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<DataId, CellData_t> pairs = (Map.Entry<DataId, CellData_t>) iter.next();
            CellData_t data = pairs.getValue();
            if( data != null )
                cellDataMap.get(pairs.getKey()).remove(data);
        }
        
        cells.remove(c.getCellId());
    }
        
    /**Removes the cell of given id
     * @param cId id of the cell to remove
     */
    public void removeCell(int cId) {
        Cell cell = cells.get(cId);
        removeCell( cell );
    }

    /**Removes all data from Data set with id DataId
     *@param id the id of the data type to clear
     */
    public void clearData(DataId id) {

        if (id.getGroup() == DataGroup.CELL) {
            Collection<? extends Data_t> dColl = cellDataMap.get(id);
            dColl.clear();
            Iterator<Map.Entry<Integer, Cell>> iter = cells.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Integer, Cell> pairs = (Map.Entry<Integer, Cell>) iter.next();
                pairs.getValue().remove(id);
            }
        } else if (id.getGroup() == DataGroup.STACK) {
            stackDataMap.put(id, null);
        }
    
    }
}
