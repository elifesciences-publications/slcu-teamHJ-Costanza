/** Case class holds Data and image Stack information that will be used by the Processor.
 */

public class Case {
    
    private Data mData;
    private Stack mStack;
    private Stack mOriginalStack;
    
    /**Case constructor.
     *@param Stack s
    */
    public Case( Stack s ){
        mOriginalStack = s;
    }
    
    /**Accessor for a working copy of Stack.
     */
    public Stack getStack(){
        return mStack;
    }
    
    /**Accessor for the original Stack.
     */
    public final Stack getOriginalStack(){
        return mOriginalStack;
    }
    
    /**Accessor for Data.
     */
    public Data getData(){
        return mData;
    }
    
    /**Set method for working copy of the Stack.
     *@param Stack s 
     */
    public void setStack( Stack s ){
        mStack = s;
    }
    
    /**Set method for Data member variable.
     *@param Data d 
     */
    public void setData( Data d ){
        mData = d;
    }
    
}