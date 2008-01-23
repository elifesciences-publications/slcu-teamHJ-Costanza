package costanza;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

/**Basin Of Atraction defines pixels that belong to the same atraction zone.
 * extends TreeSet<Pixel>, implements CellId
 */
public class BOA extends HashSet<Pixel> implements CellData_t{
    
    /**Id of the cell that data corresponds to*/
    //int cellId;
 
    Cell cell;
    /**Constructs empty BOA with id.
     *@param id int
     */
    public BOA(){
        //cellId = id;
    }
    
    /**Constructs BOA including Pixels in Collection c with id.
     * @param id int
     * @param c Collection<Pixel>
     */
    public BOA( Collection<Pixel> c ) {
        super(c);
        //cellId = id;
    }
    
    /**adds Pixel to the BOA.
     *@param p Pixel to be inserted into the BOA
     */
    public void addPixel( Pixel p ) {
        add(p);
    }
    
    /**adds Pixels from the Collection to the BOA.
     *@param c Collection<Pixel> which content is to be inserted into the BOA
     */
    public void addPixels( Collection<Pixel> c ) {
        addAll(c);
    }
    
    /**checks if the Pixel p is in the BOA.
     *@return boolean true for pixel being in the BOA
     */
    public boolean hasPixel( Pixel p ){
        return contains( p );
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
        return DataId.BOAS;
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