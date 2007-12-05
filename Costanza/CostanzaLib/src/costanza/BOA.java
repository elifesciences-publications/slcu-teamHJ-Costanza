package costanza;

import java.util.Collection;
import java.util.TreeSet;

/**Basin Of Atraction defines pixels that belong to the same atraction zone.
 * extends TreeSet<Pixel>, implements CellId
 */
public class BOA extends TreeSet<Pixel> implements CellId{
    
    /**Id of the cell that data corresponds to*/
    int cellId;
 
    /**Constructs empty BOA with id.
     *@param int id
     */
    public BOA( int id ){
        cellId = id;
    }
    
    /**Constructs BOA including Pixels in Collection c with id.
     *@param int id, Collection<Pixel> c
     */
    public BOA( int id, Collection<Pixel> c ) {
        super(c);
        cellId = id;
    }
    
    /**adds Pixel to the BOA.
     *@param Pixel p to be inserted into the BOA
     */
    public void addPixel( Pixel p ) {
        add(p);
    }
    
    /**adds Pixels from the Collection to the BOA.
     *@param Collection<Pixel> which content is to be inserted into the BOA
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
    
        /**Gets the id of the data's cell.
     *@return int
     */
    public int getId(){
        return cellId;
    }
    
//    /**Provides collection of Pixels in the BOA
//     *@return Collection<Pixels>
//     */
//    public Collection<Pixel> getPixels(){
//        return pixels;
//    }
}