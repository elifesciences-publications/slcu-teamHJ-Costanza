package costanza;

/**
 * Case class holds Data and image Stack information that will be used by the Processor.
 */
public class Case extends CellDataManipulator{

    /**Data contained in the Case.*/
    //private Data mData;
    /**Image stack contained in the Case*/
    private Stack mStack;
    /**Original image stack contained in the Case*/
    private Stack mOriginalStack;
    //private CellDataManipulator manip;

    /**
     * Case constructor.
     *
     * @param Stack s
     */
    public Case(Stack s) {
	//System.out.println("Case: Original Stack 1: " + s.getDepth());
	mOriginalStack = s;
	mStack = (Stack) s.clone();
	//mData = new Data();
	//manip = new CellDataManipulator(mData);
	//System.out.println("Case: New Stack 1: " + mStack.getDepth());
	//System.out.println("Case: Original Stack 2: " + s.getDepth());
	if (mStack == null) {
	    System.out.println("Clone is empty!");
	}
    }

//    public CellDataManipulator getManipulator() {
//	return manip;
//    }

    /**
     * Accessor for a working copy of Stack.
     *
     * @return Stack
     */
    public Stack getStack() {
	return mStack;
    }

    /**
     * Accessor for the original Stack.
     *
     * @return Stack
     */
    public final Stack getOriginalStack() {
	return mOriginalStack;
    }

    /**Accessor for Data.
     *@return Data
     */
//    public Data getData() {
//	return mData;
//    }

    /**
     * Set method for working copy of the Stack.
     *
     * @param Stack s
     */
    public void setStack(Stack s) {
	mStack = s;
    }

    /**Set method for Data member variable.
     *@param Data d
     */
//    public void setData(Data d) {
//	mData = d;
//    }
}
