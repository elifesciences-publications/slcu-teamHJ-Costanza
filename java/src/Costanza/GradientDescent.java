package Costanza;
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
     * Creates a new instance of GradientDescent
     */
    public GradientDescent() {
    }
    
    /**
     * Implementation of the Gradient descent algorithm.
     *  @param a Case to work on.
     *  @return a modified Case.
     */
    public Case process(Case c, Options o) throws Exception{
				
//  				Vector<BasinofAttractor> boa();
//  				Vector<Pixel> max();

//  				Vector<Pixel> walkTmp;//positions for one walk (start point)
//  				// Marker for which pixels that have been visited
//  				std::vector< std::vector< std::vector<int> > > flag( W() );
//  				for( int i=0 ; i<flag.size() ; ++i ) {
//  						flag[i].resize( H() );
//  						for( int j=0 ; j<flag[i].size() ; ++j ) {
//  								flag[i][j].resize( D() );
//  						}
//  				}
			
//  				//Set flag for background pixels to -1
//  				Vector<Pixel> bg();
//  				bg = c.getBackground();
//  				int bgSize = bg.size();
//  				for (int i=0; i<bgSize; ++i ) {
//  						flag[ bg.elementAt(i).getX() ][ bg.elementAt(i).getX() ]
//  								[ bg.elementAt(i).getX() ]=-1;
//  				}
			
//  				int count=1;
//  				//Find the maxima from each pixel
//  				int depth=c.getStack().getDepth();
//  				int height=c.getStack().getHeighth();
//  				int width=c.getStack().getWidth();
//  				for (int zStart=0; zStart<depth; ++zStart) {
//  						for (int yStart=0; yStart<height; ++yStart) {
//  								for (int xStart=0; xStart<width; ++xStart) {
//  										int x = xStart;
//  										int y = yStart;
//  										int z = zStart;
//  										walkTmp.resize(1);
//  										walkTmp[0].resize(3);
//  										walkTmp[0][0]=x;walkTmp[0][1]=y;walkTmp[0][2]=z;
										
//  										if (i==0 && j==0) {
//  												System.out.println("New stack.");
//  										}
//  										//find the max by walking uphill (greedy)
//  										double value,newValue;
//  										if( !flag[h][i][j] ) {
//  												do {
//  														tmpFlag[h][i][j]=1;
//  														newValue=value=c.getStack().getIntensity(x,y,z);
//  														int xNew=x, yNew=y, zNew=z;

// 														//Check all pixels around a given pixel
// // 														for (int zz=z-1; zz<=z+1; ++zz) {
// // 																for (int yy=y-1; yy<=y+1; ++yy) {
// // 																		for (int xx=x-1; xx<=x+1; ++xx) {
// // 																				if( xx>=0 && yy>=0 && zz>=0 && 
// // 																						xx<width && yy<height && zz<depth )
// // 																						if( c.getStack().getIntensity(xx,yy,zz)>newValue ) {
// // 																								newValue=c.getStack().getIntensity(xx,yy,zz);
// // 																								xNew = xx;
// // 																								yNew = yy;
// // 																								zNew = zz;
// // 																						}
// // 																		}
// // 																}
// // 														}

// 														//Check nearest neighbors
// 														for (int a=-1; a<=1; a+=2) {
// 																int zz = z+a;
// 																if( zz>=0 && zz<depth && c.>=newValue ) {
// 																		newValue=pix(hh,i,j);
// 																		newH=hh;
// 																		newI=i;
// 																		newJ=j;
// 																}
// 																int ii = i+a;
// 																if( ii>=0 && ii<H() && pix(h,ii,j)>newValue ) {
// 																		newValue=pix(h,ii,j);
// 																		newH=h;
// 																		newI=ii;
// 																		newJ=j;
// 																}
// 																int jj = j+a;  
// 																if( jj>=0 && jj<W() && pix(h,i,jj)>newValue ) {
// 																		newValue=pix(h,i,jj);
// 																		newH=h;
// 																		newI=i;
// 																		newJ=jj;
// 																}
// 														}
// 														h=newH;
// 														i=newI;
// 														j=newJ;	
// 														std::vector<int> tmpPos(3);
// 														tmpPos[0]=h;tmpPos[1]=i;tmpPos[2]=j;
// 														walkTmp.push_back( tmpPos );
// 												} while( newValue>value && !flag[h][i][j] && !tmpFlag[h][i][j] );
// 												tmpFlag[h][i][j]=1;
// 										}
										
										//XXX
														
// 	  //Check all pixels around a given pixel
// 	  for(int ii=i-1 ; ii<=i+1 ; ii++ )
// 			for(int jj=j-1 ; jj<=j+1 ; jj++ )
// 				if( ii>=0 && jj>=0 && ii<H() && jj<W() )
// 					if( pix(ii,jj)>newValue ) {
// 						newValue=pix(ii,jj);
// 						newI=ii;
// 						newJ=jj;
// 					}
	  
// 	  //Check only nearest neighbors
// //  	  for(int a=-1 ; a<=1 ; a+=2 ) {
// //  	    int ii = i+a;
// //  	    if( ii>=0 && ii<H() && pix(ii,j)>newValue ) {
// //  	      newValue=pix(ii,j);
// //  	      newI=ii;
// //  	      newJ=j;
// //  	    }
// //  	    int jj = j+a;  
// //  	    if( jj>=0 && jj<W() && pix(i,jj)>newValue ) {
// //  	      newValue=pix(i,jj);
// //  	      newI=i;
// //  	      newJ=jj;
// //  	    }
// // 		}
// 	  i=newI;
// 	  j=newJ;	
// 	  std::vector<int> tmpPos(2);
// 	  tmpPos[0]=i;tmpPos[1]=j;
// 	  walkTmp.push_back( tmpPos );
// 	} while( newValue>value && !flag[i][j] );
//       }
//       //Collect the path data and add one visit for the maximum
//       if( !flag[i][j] ) { //new maximum
// 	tmp[0]=i;tmp[1]=j;tmp[2]=1;
// 	ijTmp.push_back( tmp );
// 	int n=count++;//ijTmp.size();
// 	for( int a=0 ; a<walkTmp.size() ; a++ )
// 	  flag[ walkTmp[a][0] ][ walkTmp[a][1] ] = n;
//       }
//       else { //old maximum or background
// 	int n = flag[i][j];
// 	for( int a=0 ; a<walkTmp.size() ; a++ )
// 	  flag[ walkTmp[a][0] ][ walkTmp[a][1] ] = n;
// 	if( flag[i][j]>0 )//old maxima
// 	  ijTmp[n-1][2]++;
//       }
//     }
//   //Get the maxima visited more than threshold times and with an intensity
//   //value higher than threshold
//   std::vector<int> clusterNum;
//   for( int n=0 ; n<ijTmp.size() ; n++ )
//     if( ijTmp[n][2]>=threshold && pix(ijTmp[n][0],ijTmp[n][1])>valThreshold) {
//       ijMax.push_back( ijTmp[n] ); 
//       clusterNum.push_back( n+1 );
//     }
  
//   //Save the basins of attraction
//   boa.resize( ijMax.size() );
//   for( int i=0 ; i<H() ; i++ )
//     for( int j=0 ; j<W() ; j++ )
//       for( int n=0 ; n<ijMax.size() ; n++ )
// 	if( flag[i][j] == clusterNum[n] ) {
// 	  int tmpSize=boa[n].size();
// 	  boa[n].resize(tmpSize+1);
// 	  boa[n][tmpSize].resize(2);
// 	  boa[n][tmpSize][0]=i;
// 	  boa[n][tmpSize][1]=j;
// 	} 
  
//   return ijMax.size();
				
				
				
        return c;
    }
    
}

