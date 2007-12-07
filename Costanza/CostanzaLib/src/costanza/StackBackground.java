package costanza;

import java.util.Collection;
import java.util.Vector;

public class StackBackground extends Vector<Pixel>{
    
    /** Creates a new instance of StackBackground */
    public StackBackground(){}
    
    /**Constructs BOA including Pixels in Collection c with id.
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
    
    /**checks if the Pixel p is in the BOA.
     *@return boolean true for pixel being in the BOA
     */
    public boolean hasPixel( Pixel p ){
        return contains( p );
    }
    
//    /**Provides collection of Pixels in the background
//     *@return Collection<Pixels>
//     */
//    public Collection<Pixel> getPixels(){
//        return pixels;
//    }
}

