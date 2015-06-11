package costanza;

import java.awt.image.BufferedImage;
import java.util.Collection;

/**
 * Case class holds Data and image Stack information that will be used by the Processor.
 */
public class Case extends CellDataManipulator {

    public static final int COSTANZA_INTENSITY_LEVELS = 255;
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
    public Case(Stack s) throws Exception{
        //System.out.println("Case: Original Stack 1: " + s.getDepth());
        mOriginalStack = s;
        mStack = (Stack) s.clone();
        //initialize pixel flag data
        PixelFlag pf = new PixelFlag(mStack.getWidth(), mStack.getHeight(), mStack.getDepth());
        attachStackData(pf);
        resultImages = null;
        if (mStack == null) {
            throw new Exception("Clone stack is empty!");
        }
    }

    
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
     * @param resultImages the BufferedImage array you want to use as a resulting Image stack.
     */
    public void setResultImages(BufferedImage[] resultImages) {
        this.resultImages = resultImages;
    }
    
        /**Set a BufferedImage as a resultImage.
     * 
     * @param images Collection of BufferedImage you want to use as a resulting Image stack.
     */
    public void setResultImages(Collection<BufferedImage> images) {
        int i = 0;
        for (BufferedImage image : images) {
            this.resultImages[i++] = image;
        }
    }
}
