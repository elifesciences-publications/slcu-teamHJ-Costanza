package costanza;
import java.util.Collection;
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
	
	/**
	 * Implementation of the Gradient descent algorithm.
	 * @param c a Case to work on.
	 * @param o Options related to the Processor.
	 * @return a modified Case.
	 * @todo Recursively add plateau pixels.
	 * @todo Expand the background appropriately.
	 */
	public Case process(Case c, Options o) throws Exception {
		
			//System.out.println("GradientDescent::process");
        if( c.getStack()==null) {
            throw new Exception("No working stack initialised in case from gradientdescent");
        }
        
        int depth=c.getStack().getDepth();
        int height=c.getStack().getHeight();
        int width=c.getStack().getWidth();
        
        Vector<Pixel> max = new Vector<Pixel>();//Potential cell centers
        // Marker for which pixels that have been visited
        int [][][] flag = new int [width][height][depth];
        for (int x=0; x<width; ++x ) {
					for (int y=0; y<height; ++y ) {
						for (int z=0; z<depth; ++z ) {
							flag[x][y][z]=0;
						}
					}
        }
        //Get background and set flag for background pixels to -1
        StackBackground sb = (StackBackground)c.getStackData(DataId.stackBackground);
        Vector<Pixel> bg = null;
        if (sb!=null) {
					bg = new Vector<Pixel>(sb);
					int bgSize = bg.size();
					for (int i=0; i<bgSize; ++i ) {
						flag[ bg.elementAt(i).getX() ][ bg.elementAt(i).getY() ]
							[ bg.elementAt(i).getZ() ]=-1;
					}
        } else {
					bg = new Vector<Pixel>();
					bg.setSize(0);
        }
				//System.out.println("GradDesc: Happily extracted " + bg.size() + " background pixels.");
        int count=1;
        //Find the maxima from each pixel
        for (int zStart=0; zStart<depth; ++zStart) {
					for (int yStart=0; yStart<height; ++yStart) {
						for (int xStart=0; xStart<width; ++xStart) {
							int x = xStart;
							int y = yStart;
							int z = zStart;
							Vector<Pixel> walkTmp = new Vector<Pixel>();//positions for one walk (start point)
							walkTmp.add(new Pixel(x,y,z));
              
							//if (x==0 && y==0) {
							//System.out.println("Stack: " + z);
							//}
							//find the max by walking uphill (greedy)
							double value,newValue;
							if (flag[x][y][z]==0) {
								do {
									newValue=value=c.getStack().getIntensity(x,y,z);
									int xNew=x, yNew=y, zNew=z;
                  
									//Check all pixels around a given pixel
									for (int zz=z-1; zz<=z+1; ++zz) {
										for (int yy=y-1; yy<=y+1; ++yy) {
											for (int xx=x-1; xx<=x+1; ++xx) {
												if (xx>=0 && yy>=0 && zz>=0 &&
														xx<width && yy<height && zz<depth)
													if (c.getStack().getIntensity(xx,yy,zz)>newValue) {
														newValue=c.getStack().getIntensity(xx,yy,zz);
														xNew = xx;
														yNew = yy;
														zNew = zz;
													}
											}
										}
									}
                  
									//Check nearest neighbors
//                             for (int a=-1; a<=1; a+=2) {
//                                 int zz = z+a;
//                                 if (zz>=0 && zz<depth &&
//                                         c.getStack().getIntensity(x,y,zz)>=newValue) {
//                                     newValue = c.getStack().getIntensity(x,y,zz);
//                                     xNew=x;
//                                     yNew=y;
//                                     zNew=zz;
//                                 }
//                                 int yy = y+a;
//                                 if (yy>=0 && yy<height &&
//                                         c.getStack().getIntensity(x,yy,z)>newValue) {
//                                     newValue = c.getStack().getIntensity(x,yy,z);
//                                     xNew=x;
//                                     yNew=yy;
//                                     zNew=z;
//                                 }
//                                 int xx = x+a;
//                                 if (xx>=0 && xx<width &&
//                                         c.getStack().getIntensity(xx,y,z)>newValue) {
//                                     newValue = c.getStack().getIntensity(x,y,z);
//                                     xNew=xx;
//                                     yNew=y;
//                                     zNew=z;
//                                 }
//                             }
									x=xNew;
									y=yNew;
									z=zNew;
									walkTmp.add(new Pixel(x,y,z));
								} while (newValue>value &&
												 flag[x][y][z]==0);
							}
							
							/** @todo Recursively add plateau pixels.
							 */
							// Collect path data and add one visit for
							// the maximum
							if (flag[x][y][z]==0 ) { //new maximum
								max.add(new Pixel(x,y,z));
								int n=max.size();//count++?
								int numWalk=walkTmp.size();
								for (int a=0; a<numWalk; ++a) {
									flag[ walkTmp.elementAt(a).getX() ]
										[ walkTmp.elementAt(a).getY() ]
										[ walkTmp.elementAt(a).getZ() ] = n;
								}
							} else { //old maximum or background
								int n = flag[x][y][z];
								int numWalk=walkTmp.size();
								for (int a=0; a<numWalk; ++a) {
									flag[ walkTmp.elementAt(a).getX() ]
										[ walkTmp.elementAt(a).getY() ]
										[ walkTmp.elementAt(a).getZ() ] = n;
								}
							}
						}
					}
        }
        //Save the basins of attraction
        Vector<BOA> boa = new Vector<BOA>();
        for (int i=0; i<max.size(); ++i) {
					boa.add(new BOA(i));
        }
        for (int x=0; x<width; ++x) {
					for (int y=0; y<height; ++y) {
						for (int z=0; z<depth; ++z) {
							if (flag[x][y][z]>0) {
								//int tmpInt=flag[x][y][z]-1;
								//System.out.println(x + " " + y + " " + z + " " + tmpInt);
								boa.elementAt(flag[x][y][z]-1).addPixel(new Pixel(x,y,z));
							}
						}
					}
        }
        // Attach the cell positions to the data in the case
        Vector<CellCenter> cc = new Vector<CellCenter>();
        int numCellCenter = max.size();
        for (int i=0; i<numCellCenter; ++i) {
					cc.add(new CellCenter(i,max.get(i)));
        }
        c.attachDataCollection(DataId.cellCenters,cc);
        // Deliver the boas to the data in the case.
        c.attachDataCollection(DataId.cellBasinsOfAttraction,boa);
        
        /** @todo Expand the background appropriately.
         */
        
        //System.out.println("CellcenterCounter:" + c.sizeOfData(DataId.cellCenters));
        //System.out.println("BOACounter:" + c.sizeOfData(DataId.cellBasinsOfAttraction));
        //System.out.println("Gradient Descent found " + max.size() + " cells");
        return c;
	}
}

