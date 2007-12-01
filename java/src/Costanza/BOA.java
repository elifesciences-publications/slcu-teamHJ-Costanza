package Costanza;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**Basin Of Atraction defines pixels that belong to the same atraction zone.
 */
public class BOA extends CellId{
    
    /**Collection of pixels definig BOA.*/
    private Set<Pixel> pixels;
    
    /**Constructs empty BOA with id.
     *@param String id
     */
    public BOA( String id ){
        super(id);
        pixels = new TreeSet<Pixel>();
    }
    
    /**Constructs BOA including Pixels in Collection c with id.
     *@param String id
     */
    public BOA( String id, Collection<Pixel> c ) {
        super(id);
        pixels = new TreeSet<Pixel>(c);
    }
    
    /**adds Pixel to the BOA.
     *@param Pixel p to be inserted into the BOA
     */
    public void addPixel( Pixel p ) {
        pixels.add(p);
    }
    
    /**adds Pixels from the Collection to the BOA.
     *@param Collection<Pixel> which content is to be inserted into the BOA
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
    /**Provides collection of Pixels in the BOA
     *@return Collection<Pixels>
     */
    public Collection<Pixel> getPixels(){
        return pixels;
    }
}