package Costanza;

/**Id for the cell*/
public class CellId {
    
    /**Id of the Cell*/
    private String id;
    
    /** Creates a new instance of CellId */
    public CellId( String id ) {
        this.id = id;
    }
    
    /**retrives id of the BOA.
    *@return String
    */
    public String getId(){
        return id;
    }
}
