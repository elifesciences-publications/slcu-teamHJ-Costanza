package costanza;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

/**CellIntensity is a Vector of float intensity values, implements CellData_t*/
public class CellIntensity extends Vector<Float> implements CellData_t {

    /**Cell which owns data*/
    private Cell cell;
    /**Maps intensity tag to position in vector*/
    private Map<String, Integer> tag_map;

    /**Constructs uninitialized CellIntensity*/
    public CellIntensity( Data d) {
        tag_map = d.getIntensityTagMap();
        cell = null;
    }

    /**Constructs CellIntensity which holds specified intensity.
     *@param float intens
     */
    //public CellIntensity(String tag, float intens) {
    public CellIntensity(Data d, String tag, float intens) {
        super();
        tag_map = d.getIntensityTagMap();
        addIntensity(tag, intens);  
    }

    /**Gets the set of used intensity tags
     * @return Set<String> intensity tags
     */
    //public Set<String> getTagSet() {
    public Set<String> getTagSet() {
        return tag_map.keySet();
    }

    /**
     * Checks if tag is already in the map
     * @param tag to check for
     * @return true if given tag is already definned, false otherwise
     */
    public boolean hasTag(String tag) {
        return tag_map.containsKey(tag);
    }
    
    /**
     * Gets the position of intensity associated with a tag.
     *@param int position 
     *@return float intensity
     */
    public int getIndex(String tag) throws Exception {
        Integer pos = tag_map.get(tag);
        if (pos == null) {
            throw (new Exception("Tag: " + tag + " not in the map of available intensities."));
        }
        return pos;
    }
    /**Adds/appends new intensity to the CellIntensity
     *@param float intens
     */
    //public void addIntensity(String tag, float intens) {
    public void addIntensity(String tag, float intens) {
        Integer pos = tag_map.get(tag);
        //if ( tag_map.containsKey(tag) ) {
        if (pos != null) {
            if (pos >= size()) {
                setSize(pos + 1);
            }
            set(pos, intens);
            //System.out.println(size() + "Adding existing intensity: " + tag + " at position " + pos);
        } else {
            pos = tag_map.size();
            tag_map.put(tag, pos);
            add(intens);
            //System.out.println(size() + "Adding new intensity: " + tag + " at position " + pos);
        }

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
    //public float getIntensity(String tag) throws Exception {
    public float getIntensity(String tag) throws Exception {
        Integer pos = tag_map.get(tag);
        if (pos == null) {
            throw (new Exception("Tag: " + tag + " not in the map of available intensities."));
        }
        return elementAt(pos);
    }

    /**Sets the intensity associated with a tag to the given value.
     *@param int position 
     *@param float value
     */
    //public float setIntensity(String tag, float value) throws Exception {
    public float setIntensity(String tag, float value) throws Exception {
        Integer pos = tag_map.get(tag);
        if (pos == null) {
            throw (new Exception("Tag: " + tag + " not in the map of available intensities."));
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
