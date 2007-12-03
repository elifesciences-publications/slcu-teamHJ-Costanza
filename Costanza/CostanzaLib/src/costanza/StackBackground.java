package costanza;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class StackBackground {
    
    
    private Set<Pixel> pixels;
    
    /** Creates a new instance of StackBackground */
    public StackBackground(){
        pixels = new TreeSet<Pixel>();
    }
    
    /**Constructs BOA including Pixels in Collection c with id.
     *@param String id
     */
    public StackBackground( Collection<Pixel> c ) {
        pixels = new TreeSet<Pixel>(c);
    }
    
    /**adds Pixel.
     *@param Pixel p to be inserted
     */
    public void addPixel( Pixel p ) {
        pixels.add(p);
    }
    
    /**adds Pixels from the Collection.
     *@param Collection<Pixel> which content is to be inserted
     */
    public void addPixels( Collection<Pixel> c ) {
        pixels.addAll(c);
    }
    
    /**checks if the Pixel p is in the BOA.
     *@return boolean true for pixel being in the BOA
     */
    public boolean hasPixel( Pixel p ){
        return pixels.contains( p );
    }
    
    /**Provides collection of Pixels in the background
     *@return Collection<Pixels>
     */
    public Collection<Pixel> getPixels(){
        return pixels;
    }
}

