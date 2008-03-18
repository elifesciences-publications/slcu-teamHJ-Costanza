package costanza;

import java.util.Collection;
import java.util.Vector;
import java.util.Map;
import java.util.Set;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**Data is a container for different types of data used by Processor.
 */
public class Data {

    /**Maps dataId to the stack Data*/
    private Map<DataId, Data_t> stackDataMap;
    /**Maps cellId to the cell*/
    private Map<Integer, Cell> cells;
    /**Maps intensity tag to position in vector*/
    private Map<String, Integer> tag_map;

    public Data() {

        tag_map = new TreeMap<String, Integer>();
        cells = new TreeMap<Integer, Cell>();
        stackDataMap = new EnumMap<DataId, Data_t>(DataId.class);
        //stackDataMap.put(DataId.BACKGROUND, null);
        stackDataMap.put(DataId.PIXEL_FLAG, null);
    }
    
    /**
     * 
     * @return map asociating intensity string tags with intensity position
     */

    public Map<String, Integer> getIntensityTagMap(){
        return tag_map;
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
     */
    public Set<DataId> getDataKeys(DataGroup dg){
        DataId id[] = DataId.values();
        Set<DataId> set = new TreeSet<DataId>();
        for (int i = 0; i < id.length; ++i) {
            if( id[i].getGroup() == dg )
                set.add(id[i]);
        }
        return set;
    }

    /**Deprecated method. Avoid using it. It might be not supported in the next version and it is slow.
     * Gives the size of the data of given type
     * @param id DataId
     * @return int
     */
    public int sizeOfData(DataId id) {
        int size = 0;
        if (id.getGroup() == DataGroup.CELL) {
            Collection<Cell> cls = cells.values();
            Iterator<Cell> it = cls.iterator();

            while (it.hasNext()) {
                if (it.next().hasData(id)) {
                    ++size;
                }
            }
        } else if (id.getGroup() == DataGroup.STACK) {
            size = 1;
        }
        return size;
    }

    /**Deprecated method. Avoid using it. It might be not supported in the next version.
     * Retrives cell data Collection associated with given dataId
     * @param id DataId
     * @return Collection<? extends CellData_t >, null if no data of given type found
     */
    public Collection<? extends CellData_t > getCellData(DataId id) {
        int size = cells.size();
        Vector<CellData_t> v  = new Vector<CellData_t>(size);

        Collection<Cell> cls = cells.values();
        Iterator<Cell> it = cls.iterator();
        
        while(it.hasNext()){
            CellData_t d = it.next().get(id);
            if( d != null ){
                v.add( d );
            } 
        }
        return v;
    }

    /**Deprecated method. Avoid using it. It might be not supported in the next version.
     *Retrives cell data associated with given dataId and puts it in supplayed array
     * @param id DataId
     * @param arr array to put data in. If array is not big enough to fit data new array is created.
     * @return array containing the data
     */
    @SuppressWarnings("unchecked")
    public <T extends CellData_t> T[] getCellData(DataId id, T[] arr) {
        int size = cells.size();
        if(arr.length < size ){
            arr = (T[])java.lang.reflect.Array.newInstance(arr.getClass().getComponentType(), size);
        }
        //System.out.println("vector size = " + v.size() + "; cells size = " + cells.size());
        Collection<Cell> cls = cells.values();
        Iterator<Cell> it = cls.iterator();
        
        int counter = 0;
        while(it.hasNext()){
            CellData_t d = it.next().get(id);
            if( d != null ){
                arr[counter] = (T)d;
                ++counter;
            } 
            
        }
        return arr;
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

    /**Attaches cell data to cell with given id
     * @param data
     * @param cellId Integer
     * @param size of the new cell
     */
    public <T extends CellData_t> void attachCellData(T data, Integer cellId, short size) {

        Cell cell = cells.get(cellId);
        if (cell == null) {
            cell = new Cell(cellId, size);
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
        
        cells.put(cell.getCellId(), cell);
    }
    
    /**Removes data from data set
     * @param d Data_t
     */
    public void removeData(Data_t d) {
        DataId id = d.getDataId();
        
        if (id.getGroup() == DataGroup.CELL) {
            
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
            Iterator<Cell> iter = cells.values().iterator();
            while (iter.hasNext()) {
                iter.next().remove(id);
            }
        } else if (id.getGroup() == DataGroup.STACK) {
            stackDataMap.put(id, null);
        }
        if(id == DataId.INTENSITIES){
            tag_map.clear();
        }
    }
    
    /**Gets the set of used intensity tags
     * @return Set<String> intensity tags
     */
    public Set<String> getIntensityTagSet() {
        return tag_map.keySet();
    }

    /**
     * Checks if tag is already in the map
     * @param tag to check for
     * @return true if given tag is already definned, false otherwise
     */
    public boolean hasIntensityTag(String tag) {
        return tag_map.containsKey(tag);
    }
    
    /**
     * Gets the position of intensity associated with a tag.
     *@param int position 
     *@return float intensity
     */
    public int getIntensityIndex(String tag) throws Exception {
        Integer pos = tag_map.get(tag);
        if (pos == null) {
            throw (new Exception("Tag: " + tag + " not in the map of available intensities."));
        }
        return pos;
    }
    
    /**
     * Renumbers cell ids to consecutive numbers
     */
    public void renumberCells() {

        Set<Map.Entry<Integer, Cell>> entries = cells.entrySet();
        Iterator<Map.Entry<Integer, Cell>> iter = entries.iterator();
        Map<Integer, Cell> map = new TreeMap<Integer, Cell>();
        Integer counter = 0;
        while (iter.hasNext()) {
            Map.Entry<Integer, Cell> ent = iter.next();
            Cell c = ent.getValue();
            c.seId(counter);
            map.put( counter, c);
            ++counter;
        }
        cells = map;
    }
}
