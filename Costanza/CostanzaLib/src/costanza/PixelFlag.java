/*
 * Data type which is supposed to merge background and boa functionality
 */

package costanza;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author pawel
 */
public class PixelFlag implements Data_t {

    /**array of flags for each stack pixel*/
    private int flags[];
    /**dimensions of the stack*/
    private int xDim;
    private int yDim;
    private int zDim;
    /**increment of array index for x stored for efficiency*/ 
    private int xIncrem;
    /**definition of value for background*/
    public static final int BACKGROUND_FLAG = -1;
    public static final int UNMARKED_FLAG = -2;
    /**
     * Construct PixelFlag of given dimensions
     * @param x_dim
     * @param y_dim
     * @param z_dim
     */
    public PixelFlag( int x_dim, int y_dim, int z_dim){
        xDim = x_dim;
        yDim = y_dim;
        zDim = z_dim;
        xIncrem = yDim*zDim;
        flags = new int[ xDim * yDim * zDim ];
        for(int i = 0; i < flags.length; ++i)
            flags[i] = UNMARKED_FLAG;
    }
    /**
     * set flag at given position to the val
     * @param x
     * @param y
     * @param z
     * @param val
     */
    public void setFlag( int x, int y, int z, int val ){
        flags[x*yDim*zDim + y*zDim + z] = val;
    }
    /**
     * get flag at given position
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int getFlag( int x, int y, int z ){
         return flags[x*xIncrem + y*zDim + z];
    }
    /**
     * set flag at given position to the background value
     * @param x
     * @param y
     * @param z
     */
    public void setBackground( int x, int y, int z ){
        flags[x*yDim*zDim + y*zDim + z] = BACKGROUND_FLAG;
    }
    /**
     * check if flag at given position is set to background
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean isBackground( int x, int y, int z ){
         return flags[x*xIncrem + y*zDim + z] == BACKGROUND_FLAG;
    }
    /**
     * check if flag at given position is set to unmarked
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean isUnmarked( int x, int y, int z ){
			return flags[x*xIncrem + y*zDim + z] == UNMARKED_FLAG;
    }
    
//    /**
//     * check if flag at given position don't define a boa 
//     * @param x
//     * @param y
//     * @param z
//     * @return
//     */
//    public boolean is_not_boa( int x, int y, int z ){
//         return flags[x*xIncrem + y*zDim + z] < 0;
//    }
    
    public DataId getDataId() {
        return DataId.PIXEL_FLAG;
    }
    /**
     * 
     * @param id
     * @return Clollection of Pixels coresponding to value id
     */
    public Collection<Pixel> findPixels( int id ){
        List<Pixel> pixels = new LinkedList<Pixel>();
        for( int ix = 0; ix < xDim; ++ix ){
            for( int iy = 0; iy < yDim; ++iy ){
                for( int iz = 0; iz < zDim; ++iz ){
                    if( getFlag(ix,iy,iz) == id )
                        pixels.add( new Pixel(ix,iy,iz) );
                }
            }
        }
        return pixels;
    }
    
    /**
     * 
     * @param id
     * @return number of pixels that have flag equal id
     */
    public int count(int id) {
        int counter = 0;
        for (int ix = 0; ix < xDim; ++ix) {
            for (int iy = 0; iy < yDim; ++iy) {
                for (int iz = 0; iz < zDim; ++iz) {
                    if (getFlag(ix, iy, iz) == id) {
                        ++counter;
                    }
                }
            }
        }
        return counter;
    }
    
    /**
     * changes all values from source to dest
     * @param source value to be changed 
     * @param dest new value
     */
    public void changeAll( int source, int dest ){
        for( int ix = 0; ix < xDim; ++ix ){
            for( int iy = 0; iy < yDim; ++iy ){
                for( int iz = 0; iz < zDim; ++iz ){
                    if( getFlag(ix,iy,iz) == source )
                        setFlag(ix,iy,iz, dest);
                }
            }
        }
    }
}
