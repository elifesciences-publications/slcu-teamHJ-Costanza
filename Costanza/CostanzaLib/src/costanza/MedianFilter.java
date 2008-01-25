package costanza;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Arrays;
/**
 *
 * @author pawel
 */
public class MedianFilter extends Processor {

    @Override
    public Case process(Case c, Options options) throws Exception {

        Stack stack = c.getStack();
        if (stack == null) {
	    throw new Exception("No working stack initialised in case");
	}
        final float radius = ((Float) options.getOptionValue("medianFilterRadius")).floatValue();
	int zSize = stack.getDepth();
	int ySize = stack.getHeight();
	int xSize = stack.getWidth();
System.out.println("median filter radius: " + radius + " stack size (" + xSize + ", "  + ySize + ", " + zSize + ")");
	// Introduce a local clone	
	float[][][] localStack = new float[xSize][ySize][zSize];
	int[][][] bgFlag = new int[xSize][ySize][zSize];
	for (int zI = 0; zI < zSize; ++zI) {
	    for (int yI = 0; yI < ySize; ++yI) {
                for (int xI = 0; xI < xSize; ++xI) {
                    bgFlag[xI][yI][zI] = 0;
                }
            }
        }
        // Get background pixels from data and set bgFlag to -1
        StackBackground sb = (StackBackground) c.getStackData(DataId.BACKGROUND);
        if (sb != null) {
            int bgSize = sb.size();
            for (int i = 0; i < bgSize; ++i) {
                Pixel p = sb.elementAt(i);
                bgFlag[p.getX()][p.getY()][p.getZ()] = -1;
            }
        }

        // To save some multiplications
//	float zScale2 = stack.getZScale() * stack.getZScale();
//	float xScale2 = stack.getXScale() * stack.getXScale();
//	float yScale2 = stack.getYScale() * stack.getYScale();

        int deltaIntZ = (int) Math.ceil(radius / stack.getZScale());
        int deltaIntX = (int) Math.ceil(radius / stack.getXScale());
        int deltaIntY = (int) Math.ceil(radius / stack.getYScale());


        //define neighbours of pixel(0,0,0)
        int neighSize = (2 * deltaIntX + 1) * (2 * deltaIntY + 1) * (2 * deltaIntZ + 1);
        Vector<Pixel> neigh = new Vector<Pixel>(neighSize);
        for (int zI = -deltaIntX; zI <= deltaIntX; ++zI) {
            for (int yI = -deltaIntY; yI <= deltaIntY; ++yI) {
                for (int xI = -deltaIntX; xI <= deltaIntX; ++xI) {
                    neigh.add(new Pixel(xI, yI, zI));
                }
            }
        }

        final Pixel[] pixArr = neigh.toArray(new Pixel[neigh.size()]);
        int size = pixArr.length;

        float medArr[] = new float[neighSize];
        System.out.println("neighbour size = " + neighSize);
        for (int zI = 0; zI < zSize; ++zI) {
            for (int yI = 0; yI < ySize; ++yI) {
                for (int xI = 0; xI < xSize; ++xI) {
                    if (bgFlag[xI][yI][zI] == 0) {
                        float value = 0.0f;
                        int norm = 0;
                        for (int dzI = -deltaIntZ; dzI <= deltaIntZ; ++dzI) {
                            for (int dyI = -deltaIntY; dyI <= deltaIntY; ++dyI) {
                                for (int dxI = -deltaIntX; dxI <= deltaIntX; ++dxI) {
                                    int zII = zI + dzI;
                                    int yII = yI + dyI;
                                    int xII = xI + dxI;
                                    if (zII >= 0 && yII >= 0 && xII >= 0 &&
                                            zII < zSize && yII < ySize && xII < xSize) {
                                        //System.out.println(p);
                                        value = stack.getIntensity(xII, yII, zII);
                                        medArr[norm] = value;
                                        //medSet.add(value);
                                        ++norm;
                                    }
                                }
                            }
                        }
//                        for (int index = 0; index < size; ++index) {
//                            Pixel p = pixArr[index];
//                            int zII = zI + p.getZ();
//                            int yII = yI + p.getY();
//                            int xII = xI + p.getX();
//                            if (zII >= 0 && yII >= 0 && xII >= 0 &&
//                                    zII < zSize && yII < ySize && xII < xSize) {
//                                //System.out.println(p);
//                                value = stack.getIntensity(xII, yII, zII);
//                                medArr[norm] = value;
//                                //medSet.add(value);
//                                ++norm;
//                            }
//                        }
                        if (norm > 0) {
                            //medArr = medSet.toArray(medArr);
                            Arrays.sort( medArr, 0, norm );
                            int med = (norm-1);
                            //divide by two
                            med = med >>> 1;
                            //System.out.println("(" + xI + ", "  + yI + ", " + zI + ") : " + med + " length: " + medArr.length );
                            if ((norm & 1) != 0) {
                                value = (medArr[med] + medArr[med + 1]) / 2;
                            } else {
                                value = medArr[med];
                            }
                            localStack[xI][yI][zI] = value;
                        } else {
                            localStack[xI][yI][zI] = 0.0f;
                        }
                    } else {
                        localStack[xI][yI][zI] = 0.0f;

                    }
                }
            }
        }

        //Copy the values back to the working stack
        for (int zI = 0; zI < zSize; ++zI) {
            for (int yI = 0; yI < ySize; ++yI) {
                for (int xI = 0; xI < xSize; ++xI) {
                    stack.setIntensity(xI, yI, zI, localStack[xI][yI][zI]);
                }
            }
        }

        return c;
    }
}