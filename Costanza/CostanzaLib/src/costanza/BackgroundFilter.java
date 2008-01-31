package costanza;

/**
 * Experimental code
 * @author pawel
 */
public class BackgroundFilter extends Processor {

    @Override
    public Case process(Case c, Options options) throws Exception {  
        int lowLimit = 6;
        int hiLimit = 16;
        
        Stack stack = c.getStack();
        if (stack == null) {
            throw new Exception("No working stack initialised in case");
        }
        //final float radius = ((Float) options.getOptionValue("medianFilterRadius")).floatValue();
        boolean dim2D = false;
        if(options.hasOption("2D")){
            dim2D = ((Boolean)options.getOptionValue("2D")).booleanValue();
        }
        if(dim2D){
            lowLimit = 3;
            hiLimit = 5;
        }
        int zSize = stack.getDepth();
        int ySize = stack.getHeight();
        int xSize = stack.getWidth();
        //System.out.println("median filter radius: " + radius + " stack size (" + xSize + ", " + ySize + ", " + zSize + ")");
        // Introduce a local clone	
        //float[][][] localStack = new float[xSize][ySize][zSize];
        boolean[][][] newbg = new boolean[xSize][ySize][zSize];
        boolean[][][] bgFlag = new boolean[xSize][ySize][zSize];
        for (int zI = 0; zI < zSize; ++zI) {
            for (int yI = 0; yI < ySize; ++yI) {
                for (int xI = 0; xI < xSize; ++xI) {
                    bgFlag[xI][yI][zI] = false;
                    newbg[xI][yI][zI] = false;
                }
            }
        }
        // Get background pixels from data and set bgFlag to -1
        StackBackground sb = (StackBackground) c.getStackData(DataId.BACKGROUND);
        if (sb != null) {
            int bgSize = sb.size();
            for (int i = 0; i < bgSize; ++i) {
                Pixel p = sb.elementAt(i);
                bgFlag[p.getX()][p.getY()][p.getZ()] = true;
                newbg[p.getX()][p.getY()][p.getZ()] = true;
            }
        }

        // To save some multiplications
//	float zScale2 = stack.getZScale() * stack.getZScale();
//	float xScale2 = stack.getXScale() * stack.getXScale();
//	float yScale2 = stack.getYScale() * stack.getYScale();

//        int deltaIntZ = (int) Math.ceil(radius / stack.getZScale());
//        int deltaIntX = (int) Math.ceil(radius / stack.getXScale());
//        int deltaIntY = (int) Math.ceil(radius / stack.getYScale());

        int deltaIntZ = 1;
        int deltaIntX = 1;
        int deltaIntY = 1;
        
        //int medArr[] = new int[neighSize];
        //System.out.println("neighbour size = " + neighSize);
        for (int zI = 0; zI < zSize; ++zI) {
            for (int yI = 0; yI < ySize; ++yI) {
                for (int xI = 0; xI < xSize; ++xI) {
                    boolean value = bgFlag[xI][yI][zI];
                    int trueCount = 0;
                    int falseCount = 0;
                    //int norm = 0;
                    for (int dzI = -deltaIntZ; dzI <= deltaIntZ; ++dzI) {
                        for (int dyI = -deltaIntY; dyI <= deltaIntY; ++dyI) {
                            for (int dxI = -deltaIntX; dxI <= deltaIntX; ++dxI) {
                                int zII = zI;
                                if(!dim2D){
                                    zII += dzI;
                                }
                                int yII = yI + dyI;
                                int xII = xI + dxI;
                                if (zII >= 0 && yII >= 0 && xII >= 0 &&
                                        zII < zSize && yII < ySize && xII < xSize) {
                                    //System.out.println(p);
                                    if (!bgFlag[xII][yII][zII]) {
                                       ++falseCount;
                                    }
//                                         else {
//                                         ++trueCount;
//                                    }
                                //++norm;
                                }
                            }
                        }
                        if (dim2D) {
                            break;
                        }
                    }

                    if (!value) {
                        if (falseCount < lowLimit) {
                            newbg[xI][yI][zI] = true;
                            
                        }
                    } 
                    else {
                        if (falseCount > hiLimit) {
                            newbg[xI][yI][zI] = false;
                        }
                    }
                }
            }
        }
        
        //Copy the values back to the background
        sb.clear();
        sb.ensureCapacity(bgFlag.length);
        for (int zI = 0; zI < zSize; ++zI) {
            for (int yI = 0; yI < ySize; ++yI) {
                for (int xI = 0; xI < xSize; ++xI) {
                    if(newbg[xI][yI][zI]){
                        sb.add( new Pixel( xI, yI, zI));
                    }
                }
            }
        }
        sb.trimToSize();
        
        return c;
    }
    }
