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
    private short m_flags[];
    /**dimensions of the stack*/
    private int m_x_dim;
    private int m_y_dim;
    private int m_z_dim;
    /**increment of array index for x stored for efficiency*/ 
    private int x_increm;
    /**definition of value for background*/
    public static final short BACKGROUND_FLAG = -1;
    
    /**
     * Construct PixelFlag of given dimensions
     * @param x_dim
     * @param y_dim
     * @param z_dim
     */
    public PixelFlag( int x_dim, int y_dim, int z_dim){
        m_x_dim = x_dim;
        m_y_dim = y_dim;
        m_z_dim = z_dim;
        x_increm = m_y_dim*m_z_dim;
        m_flags = new short[ m_x_dim * m_y_dim * m_z_dim ];
        for(int i = 0; i < m_flags.length; ++i)
            m_flags[i] = 0;
    }
    /**
     * set flag at given position to the val
     * @param x
     * @param y
     * @param z
     * @param val
     */
    public void set_flag( int x, int y, int z, short val ){
        m_flags[x*m_y_dim*m_z_dim + y*m_z_dim + z] = val;
    }
    /**
     * get flag at given position
     * @param x
     * @param y
     * @param z
     * @return
     */
    public short get_flag( int x, int y, int z ){
         return m_flags[x*x_increm + y*m_z_dim + z];
    }
    /**
     * set flag at given position to the background value
     * @param x
     * @param y
     * @param z
     */
    public void set_background( int x, int y, int z ){
        m_flags[x*m_y_dim*m_z_dim + y*m_z_dim + z] = BACKGROUND_FLAG;
    }
    /**
     * check if flag at given position is set to background
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean is_background( int x, int y, int z ){
         return m_flags[x*x_increm + y*m_z_dim + z] == BACKGROUND_FLAG;
    }

    public DataId getDataId() {
        return DataId.PIXEL_FLAG;
    }
    /**
     * 
     * @param id
     * @return Clollection of Pixels coresponding to value id
     */
    public Collection<Pixel> findPixels( short id ){
        List<Pixel> pixels = new LinkedList<Pixel>();
        for( int ix = 0; ix < m_x_dim; ++ix ){
            for( int iy = 0; iy < m_y_dim; ++iy ){
                for( int iz = 0; iz < m_z_dim; ++iz ){
                    if( get_flag(ix,iy,iz) == id )
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
    public int count(short id) {
        int counter = 0;
        for (int ix = 0; ix < m_x_dim; ++ix) {
            for (int iy = 0; iy < m_y_dim; ++iy) {
                for (int iz = 0; iz < m_z_dim; ++iz) {
                    if (get_flag(ix, iy, iz) == id) {
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
    public void changeAll( short source, short dest ){
        for( int ix = 0; ix < m_x_dim; ++ix ){
            for( int iy = 0; iy < m_y_dim; ++iy ){
                for( int iz = 0; iz < m_z_dim; ++iz ){
                    if( get_flag(ix,iy,iz) == source )
                        set_flag(ix,iy,iz, dest);
                }
            }
        }
    }
}
