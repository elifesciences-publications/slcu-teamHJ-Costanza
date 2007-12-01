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

//   std::vector<int> tmp(3);
//   if( ijMax.size() )
//     ijMax.resize(0);
//   std::vector< std::vector<int> > ijTmp;//To store the values before threshold check
  
//   std::vector< std::vector<int> > walkTmp;//positions for one walk (start point)
//   // Marker for which pixels that have been visited
//   std::vector< std::vector<int> > flag( H() );
//   for( int i=0 ; i<flag.size() ; i++ )
//     flag[i].resize( W() );
  
//   //Set flag for background pixels to -1
//   for( int i=0 ; i<bg.size() ; i++ )
//     if( bg[i][0]>=0 && bg[i][0]<H() && bg[i][1]>=0 && bg[i][1]<W() )
//       flag[ bg[i][0] ][ bg[i][1] ]=-1;
  
//   int count=1;
//   //Find the maxima from each pixel
//   for( int iStart=0 ; iStart<H() ; iStart++ )
//     for( int jStart=0 ; jStart<W() ; jStart++ ) {
//       int i=iStart;
//       int j=jStart;
//       double value,newValue;
//       walkTmp.resize(1);
//       walkTmp[0].resize(2);
//       walkTmp[0][0]=i;walkTmp[0][1]=j;
      
//       //find the max by walking uphill (greedy)
//       if( !flag[i][j] ) {
// 	do {
// 	  newValue=value=pix(i,j);
// 	  int newI=i,newJ=j;
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

