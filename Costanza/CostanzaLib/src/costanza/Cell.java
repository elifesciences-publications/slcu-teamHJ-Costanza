package costanza;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**Cell groupes data for given cell*/
public class Cell implements Comparable<Cell>{

    private int id;
    private Map<DataId, CellData_t> cell_data;
    
    public Cell(int i) {
        this.id = i;
        cell_data = new EnumMap<DataId, CellData_t>(DataId.class);
        clear();
    }

    public void add(CellData_t data){
        cell_data.put( data.getDataId(), data );
    }
    
    public CellData_t get(DataId id){
        return cell_data.get( id );
    }
    
    Set<Map.Entry<DataId, CellData_t> > entrySet(){
        return cell_data.entrySet();
    }
    
    Set< DataId > keySet(){
        return cell_data.keySet();
    }
            
    public void remove(DataId id) {
        cell_data.put( id, null );
    }
    
    public void clear(){
        cell_data.put(DataId.CENTERS, null);
        cell_data.put(DataId.BOAS, null);
        cell_data.put(DataId.NEIGHBORS, null);
        cell_data.put(DataId.INTENSITIES, null);
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
