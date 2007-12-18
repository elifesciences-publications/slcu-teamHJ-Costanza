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
    
    /**Gets Set of keys of available data types for given DataGroup
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

    /**Gives the size of the data of given type
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

    /**Retrives cell data Collection associated with given dataId
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
        return cells.get(cId).get(dId);
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

    /**Attaches data Object to a dataId
     *@param id String id, 
     *@param o Object
     */
    @SuppressWarnings("unchecked")
    public <T extends CellData_t> void attachCellData(T data, Cell cell) {

        cell.add( data );
        data.setCell( cell );
        
        Collection<T> dColl = (Collection<T>) cellDataMap.get(data.getDataId());
        dColl.add( data );

    }
    
    /**Removes data object o from Data set with id DataId
     *@param Data_t data
     */
    public void removeData(Data_t d) {
        DataId id = d.getDataId();
        
        if (id.getGroup() == DataGroup.CELL) {
            Collection<? extends Data_t> dColl = cellDataMap.get(id);
            dColl.remove(d);
            
            Cell cell = ((CellData_t)d).getCell();
            cell.remove(id);
        } 
        else if (id.getGroup() == DataGroup.STACK) {
            stackDataMap.put(id, null);
        }
    }
    
    public void removeCellData(DataId dId, Cell c) {
        Data_t d = c.get(dId);
        cellDataMap.get(dId).remove(d);
	c.remove(dId);
    }
    
    public void removeCellData(DataId dId, int cId) {
        Cell cell = cells.get(cId);
        removeCellData( dId, cell );
    }
    
    public void removeAllCellData(Cell c){
        Iterator< Map.Entry<DataId, CellData_t> > iter = c.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<DataId, CellData_t> pairs = (Map.Entry<DataId, CellData_t>) iter.next();
            CellData_t data = pairs.getValue();
            if( data != null )
                cellDataMap.get(pairs.getKey()).remove(data);
        }
        
        c.clear();
    }
    
    public void removeAllCellData(int cId){
        Cell cell = cells.get(cId);
        removeAllCellData(cell);
    }
    /**Removes cell and all its data
     *@param int cId
     */
    public void removeCell(Cell c) {

        Iterator< Map.Entry<DataId, CellData_t> > iter = c.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<DataId, CellData_t> pairs = (Map.Entry<DataId, CellData_t>) iter.next();
            CellData_t data = pairs.getValue();
            if( data != null )
                cellDataMap.get(pairs.getKey()).remove(data);
        }
        
        cells.remove(c.getCellId());
    }
        
    public void removeCell(int cId) {
        Cell cell = cells.get(cId);
        removeCell( cell );
    }

    /**Removes all data from Data set with id DataId
     *@param id DataId 
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
