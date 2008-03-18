package costanza;

/**Pixel is tripple of indices. It's used by Data classes
 */
public class Pixel implements Comparable<Pixel> {

    /** Pixel indices in x, y, z directions*/
    private short x,  y,  z;

    /**Create uninitialized Pixel*/
    public Pixel() {
    }

    /**Create Pixel with indices x, y and z
     *@param x int
     *@param y int
     *@param z int
     */
    public Pixel(int x, int y, int z) {
        this.x = (short) x;
        this.y = (short) y;
        this.z = (short) z;
    }

    /**Copy constructor 
     * Create Pixel which is a copy of p*/
    public Pixel(Pixel p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    /**Get Pixel index in x direction.
     *@return int
     **/
    public int getX() {
        return x;
    }

    /**Gets Pixel index in y direction.
     *@return int
     **/
    public int getY() {
        return y;
    }

    /**Gets Pixel index in z direction.
     *@return int
     **/
    public int getZ() {
        return z;
    }

    /**Sets Pixel index in x direction.
     *@param x int
     **/
    public void setX(int x) {
        this.x = (short) x;
    }

    /**Sets Pixel index in y direction.
     *@param y int
     **/
    public void setY(int y) {
        this.y = (short) y;
    }

    /**Sets Pixel index in z direction.
     *@param z int
     **/
    public void setZ(int z) {
        this.z = (short) z;
    }

    /**Sets Pixel indices.
     *@param x int
     *@param y int
     *@param z int
     **/
    public void setXYZ(int x, int y, int z) {
        this.x = (short) x;
        this.y = (short) y;
        this.z = (short) z;
    }

	//@Override
    public int compareTo(Pixel p) {
        if (x < p.x) {
            return -1;
        } else if (x == p.x) {
            if (y < p.y) {
                return -1;
            } else if (y == p.y) {
                if (z < p.z) {
                    return -1;
                } else if (z == p.z) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object src) {
        if (!(src instanceof Pixel)) {
            return false;
        }
        Pixel p = (Pixel) src;
        return x == p.x && y == p.y && z == p.z;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.x;
        hash = 29 * hash + this.y;
        hash = 29 * hash + this.z;
        return hash;
    }
    
    @Override
    public String toString(){
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
