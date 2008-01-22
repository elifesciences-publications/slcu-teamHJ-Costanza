package costanza;

import java.util.Set;
import java.util.Vector;

/**
 * Class for finding minima/maxima by a local gradient descent/ascent
 * search.
 *
 * This is the main algorithm for segmentation in the Costanza
 * package. It uses a local search and follows the steepest gradient
 * to find maximas/minimas. It also 'segments' all pixels into
 * compartments (which are the basins of attractors (boas) for the
 * maxima. It handles the background by treating those pixels as an
 * boundary.
 * <p>
 * This class will generate data to the case in the
 * form of centers and spatial extensions (boas). It does not update
 * the working stack.
 *
 * @see Processor
 */
public class GradientDescent extends Processor {

    /**Implementation of the Gradient descent algorithm.
     * Note that no Options are needed for the Processor.
     * @param c a Case to work on.
     * @param o Options related to the Processor.
     * @return a modified Case.
     * @todo Recursively add plateau pixels.
     * @todo Expand the background appropriately.
     * @throws java.lang.Exception
     */
    @Override
    public Case process(Case c, Options o) throws Exception {

	//System.out.println("GradientDescent::process");
	if (c.getStack() == null) {
	    throw new Exception("No working stack initialised in case from gradientdescent");
	}

	int extendedNeighborhoodFlag = 1;
	//        Integer tmpFlag = (Integer) (o.getOptionValue("extendedNeighborhood"));
	//        if (tmpFlag != null) {
	//            extendedNeighborhoodFlag = tmpFlag.intValue();
	//        }

	int depth = c.getStack().getDepth();
	int height = c.getStack().getHeight();
	int width = c.getStack().getWidth();

	Vector<Pixel> max = new Vector<Pixel>();//Potential cell centers
	// Marker for which pixels that have been visited
	int[][][] flag = new int[width][height][depth];
	for (int x = 0; x < width; ++x) {
	    for (int y = 0; y < height; ++y) {
		for (int z = 0; z < depth; ++z) {
		    flag[x][y][z] = 0;
		}
	    }
	}
	//Get background and set flag for background pixels to -1
	StackBackground sb = (StackBackground) c.getStackData(DataId.BACKGROUND);
	//Vector<Pixel> bg = null;
	if (sb != null) {
	    //bg = new Vector<Pixel>(sb);
	    int bgSize = sb.size();
	    for (int i = 0; i < bgSize; ++i) {
		Pixel p = sb.elementAt(i);
		flag[p.getX()][p.getY()][p.getZ()] = -1;
	    }
	}
	//        else {
	//					bg = new Vector<Pixel>();
	//					bg.setSize(0);
	//        }
	//System.out.println("GradDesc: Happily extracted " + bg.size() + " background pixels.");
	float xFac = c.getStack().getXScale();
	float yFac = c.getStack().getYScale();
	float zFac = c.getStack().getZScale();
	if (extendedNeighborhoodFlag == 0) {
	    xFac = 1.0f / xFac;
	    yFac = 1.0f / yFac;
	    zFac = 1.0f / zFac;
	} else {
	    xFac *= xFac;
	    yFac *= yFac;
	    zFac *= zFac;
	}
	int count = 1;
	//Find the maxima from each pixel
	for (int zStart = 0; zStart < depth; ++zStart) {
	    for (int yStart = 0; yStart < height; ++yStart) {
		for (int xStart = 0; xStart < width; ++xStart) {
		    int x = xStart;
		    int y = yStart;
		    int z = zStart;
		    Vector<Pixel> walkTmp = new Vector<Pixel>();//positions for one walk (start point)
		    walkTmp.add(new Pixel(x, y, z));

		    //if (x==0 && y==0) {
		    //System.out.println("Stack: " + z);
		    //}
		    //find the max by walking uphill (greedy)
		    double value, newValue;
		    if (flag[x][y][z] == 0) {
			do {
			    newValue = value = c.getStack().getIntensity(x, y, z);
			    int xNew = x, yNew = y, zNew = z;
			    float maxGradient = 0.0f;
			    float oldIntensity = c.getStack().getIntensity(x, y, z);

			    if (extendedNeighborhoodFlag != 0) {
				//Check all pixels around a given pixel
				for (int zz = z - 1; zz <= z + 1; ++zz) {
				    for (int yy = y - 1; yy <= y + 1; ++yy) {
					for (int xx = x - 1; xx <= x + 1; ++xx) {
					    if (xx >= 0 && yy >= 0 && zz >= 0 &&
						    xx < width && yy < height && zz < depth &&
						    (x != xx || y != yy || z != zz)) {
						float newIntensity = c.getStack().getIntensity(xx, yy, zz);
						float gradient = (float) ((newIntensity - oldIntensity) /
							Math.sqrt(xFac * (xx - x) * (xx - x) +
							yFac * (yy - y) * (yy - y) +
							zFac * (zz - z) * (zz - z)));
						if (gradient > maxGradient) {
						    maxGradient = gradient;
						    newValue = newIntensity;
						    xNew = xx;
						    yNew = yy;
						    zNew = zz;
						}
					    }
					}
				    }
				}
			    } else {
				//Check nearest neighbors
				for (int a = -1; a <= 1; a += 2) {
				    int zz = z + a;
				    if (zz >= 0 && zz < depth) {
					float newIntensity = c.getStack().getIntensity(x, y, zz);
					float gradient = (newIntensity - oldIntensity) * zFac;
					if (gradient > maxGradient) {
					    maxGradient = gradient;
					    newValue = newIntensity;
					    xNew = x;
					    yNew = y;
					    zNew = zz;
					}
				    }
				    int yy = y + a;
				    if (yy >= 0 && yy < height) {
					float newIntensity = c.getStack().getIntensity(x, yy, z);
					float gradient = (newIntensity - oldIntensity) * yFac;
					if (gradient > maxGradient) {
					    maxGradient = gradient;
					    newValue = newIntensity;
					    xNew = x;
					    yNew = yy;
					    zNew = z;
					}
				    }
				    int xx = x + a;
				    if (xx >= 0 && xx < width) {
					float newIntensity = c.getStack().getIntensity(xx, y, z);
					float gradient = (newIntensity - oldIntensity) * xFac;
					if (gradient > maxGradient) {
					    maxGradient = gradient;
					    newValue = newIntensity;
					    xNew = xx;
					    yNew = y;
					    zNew = z;
					}
				    }
				}
			    }//end else
			    x = xNew;
			    y = yNew;
			    z = zNew;
			    walkTmp.add(new Pixel(x, y, z));
			} while (newValue > value &&
				flag[x][y][z] == 0);
		    }

		    /** @todo Recursively add plateau pixels.
		     */
		    // Collect path data and add one visit for
		    // the maximum
		    if (flag[x][y][z] == 0) { //new maximum
			max.add(new Pixel(x, y, z));
			int n = max.size();//count++?
			int numWalk = walkTmp.size();
			for (int a = 0; a < numWalk; ++a) {
			    flag[walkTmp.elementAt(a).getX()][walkTmp.elementAt(a).getY()][walkTmp.elementAt(a).getZ()] = n;
			}
		    } else { //old maximum or background
			int n = flag[x][y][z];
			int numWalk = walkTmp.size();
			for (int a = 0; a < numWalk; ++a) {
			    flag[walkTmp.elementAt(a).getX()][walkTmp.elementAt(a).getY()][walkTmp.elementAt(a).getZ()] = n;
			}
		    }
		}
	    }
	}
	//Save the basins of attraction
	int numCell = max.size();
	Vector<BOA> boa = new Vector<BOA>(numCell);
	//Collection<BOA> boas = (Collection<BOA>)c.getCellData(DataId.BOAS);
	for (int i = 0; i < numCell; ++i) {
	    boa.add(new BOA());
	}
	for (int x = 0; x < width; ++x) {
	    for (int y = 0; y < height; ++y) {
		for (int z = 0; z < depth; ++z) {
		    if (flag[x][y][z] > 0) {
			//int tmpInt=flag[x][y][z]-1;
			//System.out.println(x + " " + y + " " + z + " " + tmpInt);
			boa.elementAt(flag[x][y][z] - 1).addPixel(new Pixel(x, y, z));
		    }
		}
	    }
	}
	// Attach the cell positions to the data in the case
	//Vector<CellCenter> cc = new Vector<CellCenter>();

	for (int i = 0; i < numCell; ++i) {
	    // cc.add(new CellCenter(max.get(i)));
	    //System.out.println(i + ":  " + max.get(i));
	    c.attachCellData(new CellCenter(max.get(i)), i);
	    c.attachCellData(boa.elementAt(i), i);
	}

	//System.out.println("Gradient descent numCell :  " + numCell);
	//        int size1 = c.sizeOfCells();
	//        Set<Integer> keys = c.getCellIds();
	//        int size2 = keys.size();
	//        System.out.println("Gradient descent sizes :  " + size1 + "; " + size2 );
	//        System.out.println("Gradient descent set :  " + keys );
	/** @todo Expand the background appropriately.
	 */
	//System.out.println("CellcenterCounter:" + c.sizeOfData(DataId.CENTERS));
	//System.out.println("BOACounter:" + c.sizeOfData(DataId.BOAS));
	//System.out.println("Gradient Descent found " + max.size() + " cells");
	return c;
    }
}

