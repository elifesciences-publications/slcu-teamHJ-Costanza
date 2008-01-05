package costanza;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**CellIntensity is a Vector of lfoar intensity values, implements CellData_t*/
public class CellIntensity extends Vector<Float> implements CellData_t {

    /**Cell which owns data*/
    private Cell cell;
    /**Maps intensity tag to position in vector*/
    private static Map<String, Integer> tag_map = new TreeMap<String, Integer>();

    /**Constructs uninitialized CellIntensity*/
    public CellIntensity() {
    }

    /**Constructs CellIntensity which holds specified intensity.
     *@param float intens
     */
    public CellIntensity(String tag, float intens) {
        super();
        Integer pos = tag_map.get(tag);
        if (pos != null) {
            add(pos, intens);
        } else {
            pos = size();
            tag_map.put(tag, pos);
            add(intens);
        }

    }

//    /**Constructs CellIntensity with specified intensities.
//     *@param Colllection<Float> intens 
//     */
//    public CellIntensity(Collection<Float> intens) {
//        super(intens);
//    }

    /**Adds/appends new intensity to the CellIntensity
     *@param float intens
     */
    public void addIntensity(String tag, float intens) {
        Integer pos = tag_map.get(tag);
        if (pos != null) {
            add(pos, intens);
        } else {
            pos = size();
            tag_map.put(tag, pos);
            add(intens);
        }
    }

    /**Gets the position of intensity associated with a tag.
     *@param int position 
     *@return float intensity
     */
    public int getIndex(String tag)throws Exception {
        Integer pos = tag_map.get(tag);
        if (pos == null) {
            throw (new Exception("Tag: " + tag + "not in the map of available intensities."));
        }
        return pos;
    }
    
    /**Gets the associated intensity at position i.
     *@param int position 
     *@return float intensity
     */
    public float getIntensity(int pos) {
        return elementAt(pos);
    }

    /**Gets the intensity associated with a tag.
     *@param String tag 
     *@return float intensity
     */
    public float getIntensity(String tag) throws Exception {
        Integer pos = tag_map.get(tag);
        if (pos == null) {
            throw (new Exception("Tag: " + tag + "not in the map of available intensities."));
        }
        return elementAt(pos);
    }

    /**Sets the intensity associated with a tag to the given value.
     *@param int position 
     *@param float value
     */
    public float setIntensity(String tag, float value) throws Exception {
        Integer pos = tag_map.get(tag);
        if (pos == null) {
            throw (new Exception("Tag: " + tag + "not in the map of available intensities."));
        }
        return set(pos, value);
    }

    /**Sets the intensity at position i to the given value.
     *@param int position 
     *@param float value
     */
    public float setIntensity(int pos, float value) {
        return set(pos, value);
    }

    /**Gets the id of the data's cell.
     *@return int
     */
    public int getCellId() {
        return cell.getCellId();
    }

    /**Gets the id of the data.
     *@return DataId
     */
    public DataId getDataId() {
        return DataId.INTENSITIES;
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
