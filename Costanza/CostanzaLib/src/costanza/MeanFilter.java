package costanza;

import java.util.Vector;

/**
 * A mean intensity filter for smoothening the stack.
 *
 * This is a naive implementation, that just restarts calculating the
 * filter contribution from scratch. It uses a radial parameter to
 * define the sphere from which the men is calcultated.
 *
 * @see Processor
 */
public class MeanFilter extends Processor {
	
	@Override
    public Case process(Case c, Options options) throws Exception {
		//System.out.println("Mean: Original Stack: " + c.getStack().getDepth());
		Float tmpRadius = (Float) (options.getOptionValue("radius"));
		float radius = tmpRadius.floatValue();
		float radius2 = radius * radius;
		int zSize = c.getStack().getDepth();
		int ySize = c.getStack().getHeight();
		int xSize = c.getStack().getWidth();
		
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
			Vector<Pixel> bg = new Vector<Pixel>(sb);
			int bgSize = bg.size();
			for (int i=0; i<bgSize; ++i ) {
				bgFlag[ bg.elementAt(i).getX() ][ bg.elementAt(i).getY() ]
					[ bg.elementAt(i).getZ() ]=-1;
			}
		}
		
		// To save some multiplications
		double zScale2 = c.getStack().getZScale() * c.getStack().getZScale();
		double xScale2 = c.getStack().getXScale() * c.getStack().getXScale();
		double yScale2 = c.getStack().getYScale() * c.getStack().getYScale();
		
		// To be sure that all possible lattice points will be checked
		int deltaIntZ = (int) (radius / c.getStack().getZScale() + 1.0f);
		int deltaIntX = (int) (radius / c.getStack().getXScale() + 1.0f);
		int deltaIntY = (int) (radius / c.getStack().getYScale() + 1.0f);
		
		Vector<Integer> xList = new Vector<Integer>();
		Vector<Integer> yList = new Vector<Integer>();
		Vector<Integer> zList = new Vector<Integer>();
		// Collect lattice points within the sphere
		for (int zI = -deltaIntZ; zI <= deltaIntZ; ++zI) {
			for (int yI = -deltaIntY; yI <= deltaIntY; ++yI) {
				for (int xI = -deltaIntX; xI <= deltaIntX; ++xI) {
					if (zI * zI * zScale2 + yI * yI * yScale2 + xI * xI * xScale2 <= radius2) {
						zList.add(zI);
						yList.add(yI);
						xList.add(xI);
					}
				}
			}
		}
		
		int numIndex = zList.size();
		for (int zI = 0; zI < zSize; ++zI) {
			for (int yI = 0; yI < ySize; ++yI) {
				for (int xI = 0; xI < xSize; ++xI) {
					if (bgFlag[xI][yI][zI]==0) {
						float value = 0.0f;
						int norm = 0;
						for (int index = 0; index < numIndex; ++index) {
							int zII = zI + zList.elementAt(index);
							int yII = yI + yList.elementAt(index);
							int xII = xI + xList.elementAt(index);
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
