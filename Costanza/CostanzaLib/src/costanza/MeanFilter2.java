package costanza;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * A mean intensity filter for smoothening the stack.
 *
 * Variation of MenFilter
 *
 * @see Processor
 */
public class MeanFilter2 extends Processor {
	
    @Override
    public Case process(Case c, Options options) throws Exception {
        
                Stack stack = c.getStack();
		//System.out.println("Mean: Original Stack: " + c.getStack().getDepth());
		float radius = ((Float)options.getOptionValue("radius")).floatValue();
		float radius2 = radius * radius;
		int zSize = stack.getDepth();
		int ySize = stack.getHeight();
		int xSize = stack.getWidth();
		
		// Introduce a local clone
		if (c.getStack() == null) {
			throw new Exception("No working stack initialised in case");
		}
		float [][][] localStack = new float[xSize][ySize][zSize];
		int [][][] bgFlag = new int[xSize][ySize][zSize];
		for (int zI = 0; zI < zSize; ++zI) {
			for (int yI = 0; yI < ySize; ++yI) {
				for (int xI = 0; xI < xSize; ++xI) {
					bgFlag[xI][yI][zI] = 0;
				}
			}
		}
		// Get background pixels from data and set bgFlag to -1
		StackBackground sb = (StackBackground)c.getStackData(DataId.BACKGROUND);
		if (sb!=null) {
			int bgSize = sb.size();
			for (int i=0; i<bgSize; ++i ) {
				bgFlag[ sb.elementAt(i).getX() ][ sb.elementAt(i).getY() ]
					[ sb.elementAt(i).getZ() ]=-1;
			}
		}
		
		// To save some multiplications
		double zScale2 = stack.getZScale() * stack.getZScale();
		double xScale2 = stack.getXScale() * stack.getXScale();
		double yScale2 = stack.getYScale() * stack.getYScale();
		
		int deltaIntZ = (int) Math.ceil(radius / stack.getZScale());
		int deltaIntX = (int) Math.ceil(radius / stack.getXScale());
		int deltaIntY = (int) Math.ceil(radius / stack.getYScale());
		
                System.out.println("xdel :" + deltaIntX );
                System.out.println("ydel :" + deltaIntY );
                System.out.println("zdel :" + deltaIntZ );
                
                Vector<Pixel> sphere = new Vector<Pixel>();
                
                Vector<Pixel> plusX = new Vector<Pixel>();
                Vector<Pixel> minusX = new Vector<Pixel>();
                
                Vector<Pixel> plusY = new Vector<Pixel>();
                Vector<Pixel> minusY = new Vector<Pixel>();
                
                Vector<Pixel> plusZ = new Vector<Pixel>();
                Vector<Pixel> minusZ = new Vector<Pixel>();
		Vector<Integer> xList = new Vector<Integer>();
		Vector<Integer> yList = new Vector<Integer>();
		Vector<Integer> zList = new Vector<Integer>();
		// Collect lattice points within the sphere
		for (int zI = -deltaIntZ; zI <= deltaIntZ; ++zI) {
			for (int yI = -deltaIntY; yI <= deltaIntY; ++yI) {
				for (int xI = -deltaIntX; xI <= deltaIntX; ++xI) {
					if (zI * zI * zScale2 + yI * yI * yScale2 + xI * xI * xScale2 <= radius2) {
//						zList.add(zI);
//						yList.add(yI);
//						xList.add(xI);
                                            sphere.add( new Pixel(xI, yI, zI) );
					}
				}
			}
		}
		
		int size = sphere.size();
//                System.out.println("Lists size " + xList.size() + "; " + yList.size() + "; "+ zList.size() + "/n" );
//                System.out.println("xList :" + xList + "/n" );
//                System.out.println("yList :" + yList + "/n" );
//                System.out.println("zList :" + zList + "/n" );
//                
//                Set<Integer> xSet = new TreeSet<Integer>(xList);
//		Set<Integer> ySet = new TreeSet<Integer>(yList);
//		Set<Integer> zSet = new TreeSet<Integer>(zList);
//                
//                System.out.println("sets size " + xSet.size() + "; " + ySet.size() + "; "+ zSet.size() + "/n" );
//                System.out.println("xSet :" + xSet + "/n" );
//                System.out.println("ySet :" + ySet + "/n" );
//                System.out.println("zSet :" + zSet + "/n" );
                
		for (int zI = 0; zI < zSize; ++zI) {
			for (int yI = 0; yI < ySize; ++yI) {
				for (int xI = 0; xI < xSize; ++xI) {
					if (bgFlag[xI][yI][zI]==0) {
						float value = 0.0f;
						int norm = 0;
						for (int index = 0; index < size; ++index) {
							int zII = zI + sphere.elementAt(index).getZ();
							int yII = yI + sphere.elementAt(index).getY();
							int xII = xI + sphere.elementAt(index).getX();
							if (zII >= 0 && yII >= 0 && xII >= 0 &&
									zII < zSize && yII < ySize && xII < xSize) {
								value += c.getStack().getIntensity(xII, yII, zII);
								++norm;
							}
						}
						if (norm > 0) {
							localStack[xI][yI][zI] = value/norm;
						}
						else {
							localStack[xI][yI][zI] = 0.0f;
						}
					}
					else {
						localStack[xI][yI][zI]=0.0f;
					}
				}
			}
		}
		
		//Copy the values back to the working stack
		for (int zI = 0; zI < zSize; ++zI) {
			for (int yI = 0; yI < ySize; ++yI) {
				for (int xI = 0; xI < xSize; ++xI) {
					c.getStack().setIntensity(xI,yI,zI,localStack[xI][yI][zI]);
				}
			}
		}
		
		return c;
	}
}
