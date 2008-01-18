package costanza;

import java.util.Vector;

/**
 * A mean intensity filter for smoothening the stack.
 *
 * Variation of MenFilter
 *
 * @see Processor
 */
public class MeanFilter extends Processor {

    @Override
    public Case process(Case c, Options options) throws Exception {

        Stack stack = c.getStack();
        //System.out.println("Mean: Original Stack: " + c.getStack().getDepth());
        float radius = ((Float) options.getOptionValue("radius")).floatValue();
        float radius2 = radius * radius;
        int zSize = stack.getDepth();
        int ySize = stack.getHeight();
        int xSize = stack.getWidth();

        // Introduce a local clone
        if (c.getStack() == null) {
            throw new Exception("No working stack initialised in case");
        }
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
                bgFlag[sb.elementAt(i).getX()][sb.elementAt(i).getY()][sb.elementAt(i).getZ()] = -1;
            }
        }

        // To save some multiplications
        float zScale2 = stack.getZScale() * stack.getZScale();
        float xScale2 = stack.getXScale() * stack.getXScale();
        float yScale2 = stack.getYScale() * stack.getYScale();

        int deltaIntZ = (int) Math.ceil(radius / stack.getZScale());
        int deltaIntX = (int) Math.ceil(radius / stack.getXScale());
        int deltaIntY = (int) Math.ceil(radius / stack.getYScale());

//        System.out.println("xdel :" + deltaIntX);
//        System.out.println("ydel :" + deltaIntY);
//        System.out.println("zdel :" + deltaIntZ);

        Vector<Pixel> sphere = new Vector<Pixel>();
        // Collect lattice points within the sphere
        for (int zI = -deltaIntZ; zI <= deltaIntZ; ++zI) {
            for (int yI = -deltaIntY; yI <= deltaIntY; ++yI) {
                for (int xI = -deltaIntX; xI <= deltaIntX; ++xI) {
                    if (zI * zI * zScale2 + yI * yI * yScale2 + xI * xI * xScale2 <= radius2) {
                        sphere.add(new Pixel(xI, yI, zI));
                    }
                }
            }
        }

        Vector<Pixel> xShift = shift(sphere, new Pixel(1, 0, 0));
        Pixel[] plusX = getSubtraction(xShift, sphere);
        Pixel[] minusX = getSubtraction(sphere, xShift);

//
//        Vector<Pixel> yShift = shift(sphere, new Pixel(0,1,0));
//        Vector<Pixel> plusY = getSubtraction(yShift, sphere);
//        Vector<Pixel> minusY = getSubtraction(sphere, yShift);
//
//        Vector<Pixel> zShift = shift(sphere, new Pixel(1,0,0));
//        Vector<Pixel> plusZ = getSubtraction(zShift, sphere);
//        Vector<Pixel> minusZ = getSubtraction(sphere, zShift);       
//        
//        System.out.println("Sphere size :" + sphere.size());
//        System.out.println("ShiftX size :" + xShift.size());
//        System.out.println("plusX size :" + plusX.length);
//        System.out.println("minusX size :" + minusX.length);
//        
//        System.out.println("ShiftY size :" + yShift.size());
//        System.out.println("plusY size :" + plusY.size());
//        System.out.println("minusY size :" + minusY.size());
//        
//        System.out.println("ShiftZ size :" + zShift.size());
//        System.out.println("plusZ size :" + plusZ.size());
//        System.out.println("minusZ size :" + minusZ.size());
//
//
//        System.out.println("Sphere :" + sphere);
//        System.out.println("plusX :" + plusX);
//        System.out.println("minusX :" + minusX);


        final Pixel[] pixArr = sphere.toArray(new Pixel[sphere.size()]);
        int size = pixArr.length;
        int plusSize = plusX.length;
        int minusSize = minusX.length;

        for (int zI = 0; zI < zSize; ++zI) {
            for (int yI = 0; yI < ySize; ++yI) {
                for (int xI = 0; xI < xSize; ++xI) {
                    if (bgFlag[xI][yI][zI] >= 0) {
                        float value = 0.0f;
                        int norm = 0;
                        if (xI > 0 && localStack[xI - 1][yI][zI] > 0.0f) {
                            value = localStack[xI - 1][yI][zI];
                            norm = bgFlag[xI-1][yI][zI];
                            for (int index = 0; index < plusSize; ++index) {
                                Pixel p = plusX[index];
                                int zII = zI + p.getZ();
                                int yII = yI + p.getY();
                                int xII = xI + p.getX()-1;
                                if (zII >= 0 && yII >= 0 && xII >= 0 &&
                                        zII < zSize && yII < ySize && xII < xSize) {
                                    //System.out.println(p);
                                    value += stack.getIntensity(xII, yII, zII);
                                    ++norm;
                                }
                            }
                            
                            for (int index = 0; index < minusSize; ++index) {
                                Pixel p = minusX[index];
                                int zII = zI + p.getZ();
                                int yII = yI + p.getY();
                                int xII = xI + p.getX()-1;
                                if (zII >= 0 && yII >= 0 && xII >= 0 &&
                                        zII < zSize && yII < ySize && xII < xSize) {
                                    //System.out.println(p);
                                    value -= stack.getIntensity(xII, yII, zII);
                                    --norm;
                                }
                            }
                            if(norm < 0){
//                                System.out.println("negatiave norm: " + norm);
//                                System.out.println("Pixel :(" + xI + ", " + yI + ", " + zI + ")");
                                norm = 0;
                            }
                            if(value < 0.0f){
//                                System.out.println("negatiave value :" + value);
//                                System.out.println("Pixel :(" + xI + ", " + yI + ", " + zI + ")");
                                value = 0.0f;
                            }
                            localStack[xI][yI][zI] = value;
                            bgFlag[xI][yI][zI] = norm;

                        } else {
                            for (int index = 0; index < size; ++index) {
                                //Pixel p = sphere.elementAt(index);
                                Pixel p = pixArr[index];
                                int zII = zI + p.getZ();
                                int yII = yI + p.getY();
                                int xII = xI + p.getX();
                                if (zII >= 0 && yII >= 0 && xII >= 0 &&
                                        zII < zSize && yII < ySize && xII < xSize) {
                                    //System.out.println(p);
                                    value += stack.getIntensity(xII, yII, zII);
                                    ++norm;
                                }

                            }
                            if (norm > 0) {
                                localStack[xI][yI][zI] = value ;
                                bgFlag[xI][yI][zI] = norm;
                            } else {
                                localStack[xI][yI][zI] = 0.0f;
                            }
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
                    int norm = 0 ;
                    if( (norm = bgFlag[xI][yI][zI]) > 0 )
                        stack.setIntensity(xI, yI, zI, localStack[xI][yI][zI]/norm);
                    else
                        stack.setIntensity(xI, yI, zI, 0.0f);
                }
            }
        }

        return c;
    }

    private Vector<Pixel> shift(Vector<Pixel> sphere, Pixel center) {
        int size = sphere.size();
        Vector<Pixel> output = new Vector<Pixel>(size);

        for (int i = 0; i < size; ++i) {
            Pixel p = sphere.elementAt(i);
            output.add(new Pixel(p.getX() + center.getX(), p.getY() + center.getY(), p.getZ() + center.getZ()));
        }
        return output;
    }

    /**Returns vector of pixels that are present in first argument but not in second
     * @param v1 vector of pixels to be subtracted from
     * @param v2 vector of pixels that are going to be not present in the output
     * @return resulting new vector of pixels  (v = v1 - v2)
     */
    private Pixel[] getSubtraction(Vector<Pixel> v1, Vector<Pixel> v2) {
        Vector<Pixel> output = new Vector<Pixel>(v1);
        output.removeAll(v2);
        return output.toArray(new Pixel[output.size()]);
    }
}
