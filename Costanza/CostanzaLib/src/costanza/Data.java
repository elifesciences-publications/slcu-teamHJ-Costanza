package costanza;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

/**Data is a container for different types of data used by Processor.
 */
public class Data {

    /**Maps dataId to a Set of Objects which represent data*/
    //private Map< DataId, ? > mMap;
    //private int maxCellId;
    private Set<CellCenter> cellCenterData;
    private Set<BOA> cellBOAData;
    //private Set<CellCenter> cellCenterData;
    private Set<CellIntensity> cellIntensityData;
    private Set<CellNeighbors> cellNeighborData;
    private StackBackground stackBackgroundData;
    private Set<DataId> dataKeys;

    public Data() {
        cellCenterData = null;
        cellBOAData = null;
        cellIntensityData = null;
        cellNeighborData = null;
        stackBackgroundData = null;
        dataKeys = new TreeSet<DataId>();
    //mMap = new HashMap< DataId, Object >();
    //maxCellId = 0;
    }

    //public int newCellId(){
    //  return ++maxCellId;
    //}
    /**Gets Set of keys of available data types
     *
     */
    public Set<DataId> getDataKeys() {
        return dataKeys;
    }

    /**Gives the size of the data of given type
     *@param DataId id
     */
    public int sizeOfData(DataId id) {
        if (dataKeys.contains(id)) {
            if (id.name().startsWith("cell")) {
                return getCellData(id).size();
            } else if (id.name().startsWith("stack")) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**Attaches data Object to a dataId
     *@param String id, Object o
     */
    public void attachData(DataId id, Object o) throws Exception {

        switch (id) {
            case cellCenters:
                if (!dataKeys.contains(id)) {
                    cellCenterData = new TreeSet<CellCenter>();
                }
                cellCenterData.add((CellCenter) o);
                break;
            case cellBasinsOfAttraction:
                if (!dataKeys.contains(id)) {
                    cellBOAData = new TreeSet<BOA>();
                }
                cellBOAData.add((BOA) o);
                break;
            case cellIntensity:
                if (!dataKeys.contains(id)) {
                    cellIntensityData = new TreeSet<CellIntensity>();
                }
                cellIntensityData.add((CellIntensity) o);
                break;
            case cellNeighbors:
                if (!dataKeys.contains(id)) {
                    cellNeighborData = new TreeSet<CellNeighbors>();
                }
                cellNeighborData.add((CellNeighbors) o);
                break;
            case stackBackground:
                stackBackgroundData = (StackBackground) o;
                break;
            default:
                throw new Exception("Unsupported DataId in attachData.");
        }
        dataKeys.add(id);
    }

    /**Attaches all the data Objects contained in the Collection to the dataId
     *@param String id, Collection c
     */
    public void attachDataCollection(DataId id, Collection<?> c) throws Exception {

        //System.out.println("no id, Collection size: " + c.size());
        Iterator iter = c.iterator();
        switch (id) {
            case cellCenters:
                if (!dataKeys.contains(id)) {
                    cellCenterData = new TreeSet<CellCenter>();
                }
                while (iter.hasNext()) {
                    cellCenterData.add((CellCenter) iter.next());
                }
                break;
            case cellBasinsOfAttraction:
                if (!dataKeys.contains(id)) {
                    cellBOAData = new TreeSet<BOA>();
                }
                while (iter.hasNext()) {
                    cellBOAData.add((BOA) iter.next());
                }
                break;
            case cellIntensity:
                if (!dataKeys.contains(id)) {
                    cellIntensityData = new TreeSet<CellIntensity>();
                }
                while (iter.hasNext()) {
                    cellIntensityData.add((CellIntensity) iter.next());
                }
                break;
            case cellNeighbors:
                if (!dataKeys.contains(id)) {
                    cellNeighborData = new TreeSet<CellNeighbors>();
                }
                while (iter.hasNext()) {
                    cellNeighborData.add((CellNeighbors) iter.next());
                }
                break;
            default:
                throw new Exception("Unsupported DataId in attachData.");
        }
        dataKeys.add(id);
    }

    /**Removes data object o from Data set with id DataId
     *@param DataId id, Object o
     */
    public void removeData(DataId id, Object o) {
        if (dataKeys.contains(id)) {
            getCellData(id).remove(o);
            if (sizeOfData(id) == 0) {
                dataKeys.remove(id);
            }
        }
    }

    /**Removes all data from Data set with id DataId
     *@param DataId id
     */
    public void clearData(DataId id) {
        if (dataKeys.contains(id)) {
            getCellData(id).clear();
            dataKeys.remove(id);
        }
    }

    /**Retrives cell data Collection associated with dataId
     *@return Collection, null if no data of given type found
     */
    public Collection<?> getCellData(DataId id) {
        switch (id) {
            case cellCenters:
                return cellCenterData;
            case cellBasinsOfAttraction:
                return cellBOAData;
            case cellIntensity:
                return cellIntensityData;
            case cellNeighbors:
                return cellNeighborData;
        }
        return null;
    }

    /**Retrives stack data Object associated with dataId
     *@return Object, null if no data of given type found
     */
    public Object getStackData(DataId id) {
        switch (id) {
            case stackBackground:
                return stackBackgroundData;
        }
        return null;
    }
}
