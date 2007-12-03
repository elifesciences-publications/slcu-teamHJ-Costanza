package costanza;

/**Id for the cell*/
public class CellId {
    
    /**Id of the Cell*/
    private int id;
    
    /** Creates a new instance of CellId */
    public CellId( int id ) {
        this.id = id;
    }
    
    /**retrives id of the BOA.
    *@return String
    */
    public int getId(){
        return id;
    }
}
