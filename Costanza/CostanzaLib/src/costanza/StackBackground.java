package costanza;

import java.util.Collection;
import java.util.Vector;

public class StackBackground extends Vector<Pixel> implements Data_t{

    /** Creates a new instance of StackBackground */
    public StackBackground(){}
    
    /**Constructs Background including Pixels in Collection c with id.
     *@param c Collection<Pixel> 
     */
    public StackBackground( Collection<Pixel> c ) {
        super(c);
    }
    
    /**adds Pixel.
     *@param p Pixel to be inserted
     */
    public void addPixel( Pixel p ) {
        add(p);
    }
    
    /**adds Pixels from the Collection.
     *@param c Collection<Pixel> which content is to be inserted
     */
    public void addPixels( Collection<Pixel> c ) {
        addAll(c);
    }
    
    /**checks if the Pixel p is in the Background.
     *@return boolean true for pixel being in the Background
     */
    public boolean hasPixel( Pixel p ){
        return contains( p );
    }

    /**Gets the id of the data.
     *@return DataId
     */
    public DataId getDataId() {
        //return DataId.BACKGROUND;
        return null;
    }

}

