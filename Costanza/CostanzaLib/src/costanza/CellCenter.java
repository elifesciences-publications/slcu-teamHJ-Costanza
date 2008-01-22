package costanza;

/**CellCenter is a Pixel plus cell id.
 */
public class CellCenter extends Pixel implements CellData_t {

    /**Id of the cell that data corresponds to*/
    //int cellId;
    Cell cell;

    /**Constructs uninitialized CellCenter with id.
     * @param id int
     */
    public CellCenter() {
	//cellId = id;
	cell = null;
    }

    /**Constructs CellCenter at indices x, y, z and with id.
     * @param id int
     * @param x int
     * @param y int
     * @param z int
     */
    public CellCenter(int x, int y, int z) {
	super(x, y, z);
	//cellId = id;
	cell = null;
    }

    /**Constructs CellCenter at a Pixel p and with id.
     * @param id int
     * @param p Pixel
     */
    public CellCenter(Pixel p) {
	super(p);
	//cellId = id;
	cell = null;
    }

    /**Gets the id of the data's cell.
     * @return int
     */
    public int getCellId() {
	return cell.getCellId();
    }

    /**Gets the id of the data.
     * @return DataId
     */
    public DataId getDataId() {
	return DataId.CENTERS;
    }

    /**Sets the cell which owns the data.
     * @param Cell cell
     */
    public void setCell(Cell cell) {
	this.cell = cell;
    }

    /**Gets the the data's cell.
     * @return the Cell
     */
    public Cell getCell() {
	return cell;
    }
}
