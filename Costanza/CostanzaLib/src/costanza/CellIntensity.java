package costanza;

import java.util.Collection;
import java.util.Vector;

/**CellIntensity is a Vector of lfoar intensity values, implements CellData_t*/

public class CellIntensity extends Vector<Float> implements CellData_t{
    
    /**Cell which owns data*/
    Cell cell;
    /**Constructs uninitialized CellIntensity*/
    public CellIntensity(){}
    
    /**Constructs CellIntensity which holds specified intensity.
     *@param float intens
     */
    public CellIntensity( float intens ) {
        super();
        add(intens);
       
    }
    /**Constructs CellIntensity with specified intensities.
     *@param Colllection<Float> intens 
     */
    public CellIntensity( Collection<Float> intens ) {
        super(intens);
    }
    
    /**Adds/appends new intensity to the CellIntensity
     *@param float intens
     */
    public void addIntensity( float intens ){
       add(intens);
    }
    
    /**Gets the associated intensity at position i.
     *@param int position 
     *@return float intensity
     */
    public float getIntensity( int pos ){
        return elementAt(pos);
    }  
    
    /**Sets the intensity at position i to the given value.
     *@param int position 
     *@param float value
     */
    public float setIntensity( int pos, float value ){
        return set(pos, value);
    }
    
   /**Gets the id of the data's cell.
     *@return int
     */
    public int getCellId(){
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
