package costanza;

/**Applies 3D median filter to the Stack of Images stored in Case.
 * The options parameter should contain "medianFilterRadius" that controls the radius of the filter. 
 * Processes the whole images, not leaving out the background pixels.
 * @param c the Case to operate on.
 * @param options the Options this Processor needs.
 * @return the processed Case.
 * @throws java.lang.Exception
 * @see Processor
 * @author pawel
 */
public class MedianFilter extends Processor {

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
        final float radius = ((Float) options.getOptionValue("medianFilterRadius")).floatValue();
        final int repeat = ((Integer) options.getOptionValue("medianFilterRepeat")).intValue();

        int zSize = stack.getDepth();
        int ySize = stack.getHeight();
        int xSize = stack.getWidth();
//        System.out.println("median filter radius: " + radius + " stack size (" + xSize + ", " + ySize + ", " + zSize + ")");
        // Introduce a local variables

        makeKernel(radius);
        int kYSize = 2 * kYRadius + 1;
        int kZSize = 2 * kZRadius + 1;
        int xmin = -kXRadius;
        int xmax = xSize + kXRadius;
        int cacheWidth = xmax - xmin;

        float[] medianBuf1 = new float[kNPoints];
        float[] medianBuf2 = new float[kNPoints];
        int[] newLineRadius0 = new int[kYSize];

        float cache[] = new float[kZSize * kYSize * cacheWidth]; // 3D image stripe

        for (int rep = 0; rep < repeat; ++rep) {
            //Fill in first stripe
            for (int y = -kYRadius,  iCache = 0; y < kYRadius; ++y) {
                for (int z = -kZRadius; z <= kZRadius; ++z) {
                    Image img = stack.getImage(z < 0 ? 0 : z >= zSize ? zSize - 1 : z);

                    for (int x = xmin; x < xmax; ++x, ++iCache) {
                        cache[iCache] = img.getIntensity(x < 0 ? 0 : x >= xSize ? xSize - 1 : x, y < 0 ? 0 : y >= ySize ? ySize - 1 : y);
                    }
                }
            }

            int nextPlaneInCache = 2 * kYRadius; //next X-Z plane to fill in
            float median = cache[0];
            for (int z = 0; z < zSize; ++z) {

                for (int y = 0; y < ySize; ++y) {
                    int ynext = y + kYRadius;
                    if (ynext >= ySize) {
                        ynext = ySize - 1;
                    }

                    int iCache = cacheWidth * kZSize * nextPlaneInCache; //place in chache where nextPlaneInCache is
                    //fill the X-Z plane
                    for (int curZ = z - kZRadius; curZ <= z + kZRadius; ++curZ) {
                        Image img = stack.getImage(curZ < 0 ? 0 : curZ >= zSize ? zSize - 1 : curZ);
                        float pixels[] = img.getPixels();
                        //pixels out of bounds replaced by the edge values
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
                    nextPlaneInCache = (nextPlaneInCache + 1) % kYSize; //wrap around kernel to the next plane

                    Image img = stack.getImage(z);
                    float pixels[] = img.getPixels();

                    for (int x = 0,  p = x + y * xSize,  xCache0 = kXRadius; x < xSize; x++, p++, xCache0++) {
                        median = getMedian(cache, cacheWidth, xCache0, lineRadius, kYSize, kZSize, medianBuf1, medianBuf2, median);
                        pixels[p] = median;
                    }//for x
                    System.arraycopy(lineRadius, (kYSize - 1) * kZSize, newLineRadius0, 0, kYSize);
                    System.arraycopy(lineRadius, 0, lineRadius, kYSize, (kYSize - 1) * kZSize);
                    System.arraycopy(newLineRadius0, 0, lineRadius, 0, kYSize);
                }//for y
            }//for z
        }
        return c;
    }

    /**
     * Calculates median value of kernel contained in cache
     * @param cache array containing data
     * @param cacheWidth width of data array (x dimension)
     * @param xCache0 center of kernel in data array
     * @param lineRadius array cointaing radi inx direction of kernel lines 
     * @param kYSize kernel size in y direction
     * @param kZSize kernel size in z direction
     * @param aboveBuf buffer for values larger than guess
     * @param belowBuf buffer for values smaller than guess
     * @param guess value used to pivot kernel data
     * @return median value
     */
    private float getMedian(float[] cache, int cacheWidth, int xCache0, int[] lineRadius, int kYSize, int kZSize, float[] aboveBuf, float[] belowBuf, float guess) {
        int half = kNPoints / 2;
        int nAbove = 0, nBelow = 0;
        int yIncr = cacheWidth * kZSize;
        for (int y = 0; y < kYSize; y++) {
            int yInd = y * yIncr;
            int yIncr2 = y * kZSize;
            for (int z = 0; z < kZSize; z++) {
                int lineInd = yIncr2 + z;
                for (int x = xCache0 - lineRadius[lineInd],  iCache = yInd + z * cacheWidth + x; x <= xCache0 + lineRadius[lineInd]; x++, iCache++) {
                    float v = cache[iCache];
                    if (v > guess) {
                        aboveBuf[nAbove] = v;
                        nAbove++;
                    } else if (v < guess) {
                        belowBuf[nBelow] = v;
                        nBelow++;
                    }
                }
            }
        }
        if (nAbove > half) {
            return findNthLowestNumber(aboveBuf, nAbove, nAbove - half - 1);
        } else if (nBelow > half) {
            return findNthLowestNumber(belowBuf, nBelow, half);
        } else {
            return guess;
        }
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
        int yIncr = kYRadius * kZSize;
        lineRadius = new int[kZSize * kYSize];
        lineRadius[yIncr + kZRadius] = kXRadius;
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

    /** Find the n-th lowest number in part of an array
     *  @param buf The input array. Only values 0 ... bufLength are read. <code>buf</code> will be modified.
     *  @param bufLength Number of values in <code>buf</code> that should be read
     *  @param n which value should be found; n=0 for the lowest, n=bufLength-1 for the highest
     *  @return the value */
    public static float findNthLowestNumber(float[] buf, int bufLength, int n) {
        // Courtesy of ImageJ source code
        // Modified algorithm according to http://www.geocities.com/zabrodskyvlada/3alg.html
        // Contributed by Heinz Klar
        int i, j;
        int l = 0;
        int m = bufLength - 1;
        float med = buf[n];
        float dum;

        while (l < m) {
            i = l;
            j = m;
            do {
                while (buf[i] < med) {
                    i++;
                }
                while (med < buf[j]) {
                    j--;
                }
                dum = buf[j];
                buf[j] = buf[i];
                buf[i] = dum;
                i++;
                j--;
            } while ((j >= n) && (i <= n));
            if (j < n) {
                l = i;
            }
            if (n < i) {
                m = j;
            }
            med = buf[n];
        }
        return med;
    }
}
