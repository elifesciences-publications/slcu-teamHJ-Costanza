package costanza;

    /**Smoothens the Stack of Images residing in the Case by applying 3D mean filter to it. 
     * The supplied Option object should contain:
     * a "radius" that controls the radius of the mean filter.  
     * Example: options.addOption("radius", new Float(2));
     * Omits the pixels in the Background.
     * @param c the Case to operate on.
     * @param options the Options this Processor needs.
     * @return the processed Case.
     * @throws java.lang.Exception
     * @see Processor
     */

public class MeanFilter extends Processor {

    private int lineRadius[];
    private int kXRadius;
    private int kYRadius;
    private int kZRadius;
    private int kNPoints;
    private Stack stack;

    @Override
    public Case process(Case c, Options options) throws Exception {

        stack = c.getStack();
        if (stack == null) {
            throw new Exception("No working stack initialised in case");
        }
        final float radius = ((Float) options.getOptionValue("radius")).floatValue();
        int zSize = stack.getDepth();
        int ySize = stack.getHeight();
        int xSize = stack.getWidth();

        // Get background pixels from data and set bgFlag to -1
        int zIncr = ySize * xSize;
        int[] bgFlag = new int[xSize * ySize * zSize];

        StackBackground sb = (StackBackground) c.getStackData(DataId.BACKGROUND);
        if (sb != null) {
            int bgSize = sb.size();
            for (int i = 0; i < bgSize; ++i) {
                Pixel p = sb.elementAt(i);
                bgFlag[p.getX() + xSize * p.getY() + zIncr * p.getZ()] = -1;
            }
        }
        //prepare spherical kernel
        makeKernel(radius);

        int kYSize = 2 * kYRadius + 1;
        int kZSize = 2 * kZRadius + 1;
        int xmin = -kXRadius;
        int xmax = xSize + kXRadius;
        int cacheWidth = xmax - xmin;

        int[] newLineRadius0 = new int[kZSize];

        float cache[] = new float[kZSize * kYSize * cacheWidth]; // 3D image stripe

        for (int y = -kYRadius,  iCache = 0; y < kYRadius; ++y) {
            for (int z = -kZRadius; z <= kZRadius; ++z) {
                Image img = stack.getImage(z < 0 ? 0 : z >= zSize ? zSize - 1 : z);

                for (int x = xmin; x < xmax; ++x, ++iCache) {	 // 
                    cache[iCache] = img.getIntensity(x < 0 ? 0 : x >= xSize ? xSize - 1 : x, y < 0 ? 0 : y >= ySize ? ySize - 1 : y);
                }
            }
        }
        int nextPlaneInCache = 2 * kYRadius;
        float mean = 0;
        for (int z = 0; z < zSize; ++z) {

            for (int y = 0; y < ySize; ++y) {
                int ynext = y + kYRadius;
                if (ynext >= ySize) {
                    ynext = ySize - 1;
                }

                int iCache = cacheWidth * kZSize * nextPlaneInCache;

                for (int curZ = z - kZRadius; curZ <= z + kZRadius; ++curZ) {

                    Image img = stack.getImage(curZ < 0 ? 0 : curZ >= zSize ? zSize - 1 : curZ);
                    float pixels[] = img.getPixels();

                    float leftpxl = img.getIntensity(0, ynext);
                    float rightpxl = img.getIntensity(xSize - 1, ynext);

                    for (int x = xmin; x < 0; x++, iCache++) {
                        cache[iCache] = leftpxl;
                    }
                    System.arraycopy(pixels, xSize * ynext, cache, iCache, xSize);
                    iCache += xSize;
                    for (int x = xSize; x < xmax; x++, iCache++) {
                        cache[iCache] = rightpxl;
                    }
                }
                nextPlaneInCache = (nextPlaneInCache + 1) % kYSize;

                Image img = stack.getImage(z);
                float pixels[] = img.getPixels();
                boolean fullCalculation = true;

                for (int x = 0,  p = x + y * xSize,  xCache0 = kXRadius; x < xSize; x++, p++, xCache0++) {
                    if (bgFlag[p + z * zIncr] >= 0) {
                        if (fullCalculation) {

                            mean = getAreaSums(cache, cacheWidth, xCache0, lineRadius, kYSize, kZSize);
                        } else {

                            mean = addSideSums(cache, cacheWidth, xCache0, lineRadius, kYSize, kZSize, mean);
                        }
                        pixels[p] = mean / (float) kNPoints;
                        fullCalculation = false;
                    } else {
                        fullCalculation = true;
                        pixels[p] = 0.0f;
                    }
                }//for x

                System.arraycopy(lineRadius, (kYSize - 1) * kZSize, newLineRadius0, 0, kZSize);
                System.arraycopy(lineRadius, 0, lineRadius, kZSize, (kYSize - 1) * kZSize);
                System.arraycopy(newLineRadius0, 0, lineRadius, 0, kZSize);

            }//for y
        }//for z

        return c;

    }

    /**
     * Calculates sum of values cointained within kernel area
     * @param cache array containing data
     * @param cacheWidth width of data array (x dimension)
     * @param xCache0 center of kernel in data array
     * @param lineRadius array cointaing radi inx direction of kernel lines 
     * @param kYSize kernel size in y direction
     * @param kZSize kernel size in z direction
     * @return sum
     */
    private float getAreaSums(float[] cache, int cacheWidth, int xCache0, int[] lineRadius, int kYSize, int kZSize) {
        float sum = 0;
        int yIncr = cacheWidth * kZSize;
        for (int y = 0; y < kYSize; y++) {
            int yInd = y * yIncr;
            int yIncr2 = y * kZSize;
            for (int z = 0; z < kZSize; z++) {
                int lineInd = yIncr2 + z;
                for (int x = xCache0 - lineRadius[lineInd],  iCache = yInd + z * cacheWidth + x; x <= xCache0 + lineRadius[lineInd]; x++, iCache++) {
                    float v = cache[iCache];
                    sum += v;
                }
            }
        }
        return sum;
    }

    /**
     * Adds values on the right border and removes values from the left border of previously calculated sum of the kernel area.
     * @param cache array containing data
     * @param cacheWidth width of data array (x dimension)
     * @param xCache0 center of kernel in data array
     * @param lineRadius array cointaing radi inx direction of kernel lines 
     * @param kYSize kernel size in y direction
     * @param kZSize kernel size in z direction
     * @param sum previous value
     * @return new sum
     */
    private float addSideSums(float[] cache, int cacheWidth, int xCache0, int[] lineRadius, int kYSize, int kZSize, float sum) {
        int yIncr = cacheWidth * kZSize;
        for (int y = 0; y < kYSize; y++) {
            int yInd = y * yIncr;
            int yIncr2 = y * kZSize;
            for (int z = 0; z < kZSize; z++) {
                int lineInd = yIncr2 + z;
                if (lineRadius[lineInd] >= 0) {
                    int iCache0 = yInd + z * cacheWidth + xCache0;
                    float v = cache[iCache0 + lineRadius[lineInd]];
                    sum += v;
                    v = cache[iCache0 - lineRadius[lineInd] - 1];
                    sum -= v;
                }
            }
        }

        return sum;
    }

    /**
     * Prepares spherical kernel for processor. Kernel marks points that will be used in by filter.
     * Fills in values of member variables: lineRadius[], kXRadius, kYRadius, kZRadius, kNPoints
     * @param radius for the kernel
     */
    private void makeKernel(float radius) {

        float radius2 = radius * radius;

        kXRadius = (int) ((radius + 1e-10) / stack.getXScale());
        kYRadius = (int) ((radius + 1e-10) / stack.getYScale());
        kZRadius = (int) ((radius + 1e-10) / stack.getZScale());

        kXRadius = kXRadius == 0 ? 1 : kXRadius;
        kYRadius = kYRadius == 0 ? 1 : kYRadius;
        kZRadius = kZRadius == 0 ? 1 : kZRadius;

        float zScale2 = stack.getZScale() * stack.getZScale();
        float xScale = stack.getXScale();
        float yScale2 = stack.getYScale() * stack.getYScale();

        int kZSize = 2 * kZRadius + 1;
        int kYSize = 2 * kYRadius + 1;
        
        //fill in values for y=0, z=0 
        int yIncr = kYRadius* kZSize;
        lineRadius = new int[kZSize * kYSize];
        lineRadius[ yIncr + kZRadius] = kXRadius;
        kNPoints = (2 * kXRadius + 1);

        for (int y = 1; y <= kYRadius; ++y) {
            int y2 = y * y;
            int yInd1 = (kYRadius - y) * kZSize;
            int yInd2 = (kYRadius + y) * kZSize;

            //fill in values for z=0 
            double radf = (radius2 - y2 * yScale2) / xScale + 1e-10;
            int rad = radf < 0.0 ? -1 : (int) Math.sqrt(radf);

            lineRadius[yInd1 + kZRadius] = rad;
            lineRadius[yInd2 + kZRadius] = rad;

            kNPoints += rad < 0 ? 0 : 4 * rad + 2;

            for (int z = 1; z <= kZRadius; ++z) {
                int zInd1 = (kZRadius - z);
                int zInd2 = (kZRadius + z);
                int z2 = z * z;

                radf = (radius2 - z2 * zScale2 - y2 * yScale2) / xScale + 1e-10;
                rad = radf < 0.0 ? -1 : (int) Math.sqrt(radf);

                lineRadius[yInd1 + zInd1] = rad;
                lineRadius[yInd2 + zInd1] = rad;
                lineRadius[yInd1 + zInd2] = rad;
                lineRadius[yInd2 + zInd2] = rad;

                kNPoints += rad < 0 ? 0 : 8 * rad + 4;
            }
        }

        //fill in values for y=0 
        for (int z = 1; z <= kZRadius; ++z) {
            double radf = (radius2 - z * z * zScale2) / xScale + 1e-10;
            int rad = radf < 0.0 ? -1 : (int) Math.sqrt(radf);

            lineRadius[yIncr + kZRadius - z] = rad;
            lineRadius[yIncr + kZRadius + z] = rad;

            kNPoints += rad < 0 ? 0 : 4 * rad + 2;
        }
        
//        for (int y = 0; y < kYSize; ++y) {
//            for (int z = 0; z < kZSize; ++z) {
//                System.out.print(lineRadius[y*kZSize+z] + ",");
//            }
//            System.out.println();
//        }
    }
}
