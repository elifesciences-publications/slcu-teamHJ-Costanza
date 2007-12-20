package costanza;

import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * Case class holds Data and image Stack information that will be used by the Processor.
 */
public class Case extends CellDataManipulator {

    /**Data contained in the Case.*/
    //private Data mData;
    /**Image stack contained in the Case*/
    private Stack mStack;
    /**Original image stack contained in the Case*/
    private Stack mOriginalStack;
    //private CellDataManipulator manip;
    /**A BufferedImage array serving as a result for certain Processors.*/
    private BufferedImage[] resultImages;

    /**
     * Case constructor.
     *
     * @param s a Stack of Image.
     */
    public Case(Stack s) {
        //System.out.println("Case: Original Stack 1: " + s.getDepth());
        mOriginalStack = s;
        mStack = (Stack) s.clone();
        //mData = new Data();
        //manip = new CellDataManipulator(mData);
        //System.out.println("Case: New Stack 1: " + mStack.getDepth());
        //System.out.println("Case: Original Stack 2: " + s.getDepth());
        resultImages = null;
        if (mStack == null) {
            System.out.println("Clone is empty!");
        }
    }

    /*
    public CellDataManipulator getManipulator() {	
    return manip;
    }
     */
    
    /**Accessor for a working copy of Stack.
     *
     * @return the working Stack.
     */
    public Stack getStack() {
        return mStack;
    }

    /**Accessor for the original Stack.
     *
     * @return Stack
     */
    public final Stack getOriginalStack() {
        return mOriginalStack;
    }

    /**Accessor for Data.
     *@return Data
     *
    public Data getData() {
    return mData;
    }
     */
    /**Set method for working copy of the Stack.
     *
     * @param s Stack
     */
    public void setStack(Stack s) {
        mStack = s;
    }

    /**Set method for Data member variable.
     *@param Data d
     *
    public void setData(Data d) {
    mData = d;
    }
     */
    /**Return a Vector of BufferedImage which might be a result of a Processor.
     * 
     * @return a BufferedImage Vector representing the result of a Processor.
     */
    public BufferedImage[] getResultImages() {
        return resultImages;
    }

    /**Set a BufferedImage as a resultImage.
     * 
     * @param resultImage the BufferedImage you want to use as a resulting Image.
     */
    public void setResultImages(BufferedImage[] resultImages) {
        this.resultImages = resultImages;
    }
}
