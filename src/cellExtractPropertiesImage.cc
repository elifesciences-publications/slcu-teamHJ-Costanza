/**
 * Filename     : cellExtractPropertiesImage.cc
 * Description  : Finds cells in a wall image and use the basin of attraction 
                : to define neighbors and walls
 * Author(s)    : Henrik Jonsson (henrik@thep.lu.se)
 * Created      : January 2005
 * Revision     : $Id$
 */
#include"image.h"
#include<iostream>
#include<fstream>
#include<cmath>

int removeDuplicateCells( std::vector< std::vector<int> > &dataIn,
			  std::vector< std::vector<int> > &dataOut, 
			  std::vector< std::vector< std::vector<int> > > 
			  &boaIn, 
			  std::vector< std::vector< std::vector<int> > > 
			  &boaOut, 
			  double r0);
void addNeighbors(int i, std::vector<int> &mark,std::vector< std::vector<int> >
		  &neigh,
		  std::vector< std::vector<int> > &groups);
void createWallsFromBOAs(int H,int W,
			 std::vector< std::vector< std::vector<int> > > &boa,
			 std::vector< std::vector< std::vector<int> > > &walls,
			 std::vector< std::vector< std::vector<int> > > 
			 &compartmentBoa,
			 std::vector< std::vector<int> > &neigh,
			 std::vector< std::vector<double> > &compartment, 
			 double radius=1.1);
void createNeighborsFromBOAs(int H,int W,
			     std::vector< std::vector< std::vector<int> > > 
			     &boa,
			     std::vector< std::vector< std::vector<int> > > 
			     &compartmentBoa,
			     std::vector< std::vector<double> > &compartment);
void extractCellWallInformation(int H,int W,
				std::vector< std::vector< std::vector<int> > > 
				&boa,
				std::vector< std::vector< std::vector<int> > > 
				&walls,
				std::vector< std::vector< std::vector<int> > > 
				&compartments,
				double wallRadius=1.1);
void wallsFromPixelsToEndpoints( std::vector< std::vector< std::vector<int> > >
				 &walls,
				 std::vector< std::vector<int> > &endpoints);
void gatherWallsForCells( std::vector< std::vector< std::vector<int> > > 
			  &walls,
			  std::vector< std::vector<int> > &wallPixelCell,
			  std::vector< std::vector< std::vector<int> > > 
			  &cellWalls);
void cellCenterFromPixels( std::vector< std::vector< std::vector<int> > > 
			   &pixels,
			   std::vector< std::vector<int> > &cellCenter);
void calculatePolarization( std::vector< std::vector< std::vector<int> > > 
			    &pixels,
			    std::vector< std::vector<int> > &cellCenter,
			    std::vector< std::vector<double> > &polarization);
void readPixelPos( char* file, std::vector< std::vector<int> > &pixels );
void printWallPolarization( Image &IA, Image &IB,
			    std::vector< std::vector< 
			    std::vector<int> > > &walls,
			    std::vector< std::vector<int> >
			    & wallPixelCell);
void getCompartmentConcentrations( Image &I,
				   std::vector< std::vector< 
				   std::vector<int> > > &compartmentBoa,
				   std::vector<double> &compartmentIntensity);
void writeCompartmentPixels(char* file,
			    std::vector< std::vector<double> > &compartment,
			    std::vector< std::vector< std::vector<int> > > 
			    &boa,
			    std::vector< std::vector< std::vector<int> > > 
			    &walls);

int main( int argc,char* argv[] ) {
  
  if( argc<3 || argc>9 ) {
    std::cerr << "Usage: " << argv[0] << " imageFile bgPixelsFile "
	 << "[smothenRadius] [smothenWeight] [smothenNum]" 
	 << " [numThreshold] [intensityThreshold] [neighborRadius]\n"; 
    exit(-1);
  }
  double weight=1.;
  int numSmothen=1;
  double radius=1.;
  int numThreshold=1;
  double intensityThreshold=0.;
  double neighborRadius=0.;
  if( argc>3 )
    radius = atof( argv[3] );
  if( argc>4 )
    weight = atof( argv[4] );
  if( argc>5 )
    numSmothen = atoi( argv[5] );
  if( argc>6 )
    numThreshold = atoi( argv[6] );
  if( argc>7 )
    intensityThreshold = atof( argv[7] );
  if( argc>8 )
    neighborRadius = atof( argv[8] );
  
  Image IOri( argv[1],0 );
  Image I( IOri );

  I.invert();

  //Smothening of the image (sphere template method?)
  //////////////////////////////////////////////////////////////////////
  if( weight>=0 && numSmothen ) {
    std::cerr << "Smothening image " << numSmothen << " time(s) with weight " 
	      << weight << " and radius " << radius << "\n";
    for( int i=0 ; i<numSmothen ; i++ )
      I.smothen(weight,radius);
  }
  else
    std::cerr << "No smothening of image...\n";
  
  //Read the background pixels
  //////////////////////////////////////////////////////////////////////
  std::vector< std::vector<int> > bg;
  std::cerr << "Reading background pixels...";
  readPixelPos(argv[2],bg);
  std::cerr << "done!\n";
  
  //Gradient descent (watershed) with post processor to remove too 
  // small/dark cells
  //////////////////////////////////////////////////////////////////////
  std::vector< std::vector<int> > cellCenter;
  std::vector< std::vector< std::vector<int> > > boa;
  std::cerr << "Running gradient descent...";
  I.findMaximaByGradientDescent(cellCenter,boa,bg,
				intensityThreshold,numThreshold);
  std::cerr << "done!\n";
  std::cerr << cellCenter.size() << " cells found in image\n"; 
  std::cerr << "Plotting found cells in " << "/tmp/cellTmp.tif" << "\n"; 
  IOri.writeImageTiffWithRandomColors("/tmp/cellTmp.tif",boa);
  
  //Add pixel intensity to each cell center
  for( int n=0 ; n<cellCenter.size() ; n++ )
    cellCenter[n].push_back( int(I.pix(cellCenter[n][0],cellCenter[n][1])) );

  //Combine cells that have their centers too close to each other
  //////////////////////////////////////////////////////////////////////
  std::vector< std::vector<int> > cellCenterFinal;
  std::vector< std::vector< std::vector<int> > > boaFinal;
  removeDuplicateCells( cellCenter, cellCenterFinal, boa, boaFinal,
			neighborRadius );
  std::cerr << "Plotting final cells in /tmp/cellFinalTmp.tif\n"; 
  IOri.writeImageTiffWithRandomColors("/tmp/cellFinalTmp.tif",
				      boaFinal);
  
  //Print the cell centers
  //////////////////////////////////////////////////////////////////////
  //std::cout << cellCenterFinal.size() << " " << 2 << "\n";
  //for(int i=0 ; i<cellCenterFinal.size() ; i++ )
  //std::cout << cellCenterFinal[i][0] << " " << cellCenterFinal[i][1] << "\n";
  //exit(0);
  
  
  //Extract compartmental information for a simulation template
  //////////////////////////////////////////////////////////////////////
  std::vector< std::vector< std::vector<int> > > walls;
  std::vector< std::vector<int> > neigh( cellCenterFinal.size() ), 
    wallPixelCell;
  std::vector< std::vector< double > > compartment;
  std::vector< std::vector< std::vector<int> > > compartmentBoa;
  double wallR=0.0;
  if( wallR>0 ) {
    createWallsFromBOAs(I.H(),I.W(),boaFinal,walls,
			compartmentBoa,neigh,compartment,1.0);
  }
  else {
    createNeighborsFromBOAs(I.H(),I.W(),boaFinal,compartmentBoa,compartment);
  }
  if( walls.size() ) {
    std::cerr << walls.size()  << " walls in total.\n";
    std::cerr << "Plotting found walls in /tmp/wallTmp.tif\n"; 
    IOri.writeImageTiffWithRandomColors("/tmp/wallTmp.tif",walls);
  }
  std::cerr << "Writing compartment pixels in " << "/tmp/cell.compartment"
	    << "\n";
  writeCompartmentPixels("/tmp/cell.compartment",compartment,boaFinal,walls);
  
  std::cerr << "Plotting all compartments in /tmp/compTmp.tif\n"; 
  IOri.writeImageTiffWithRandomColors("/tmp/compTmp.tif",compartmentBoa);
}

//! Remove cells that are closer than r0 and combining basins of attraction
int removeDuplicateCells( std::vector< std::vector<int> > &dataIn,
			  std::vector< std::vector<int> > &dataOut, 
			  std::vector< std::vector< std::vector<int> > > 
			  &boaIn,
			  std::vector< std::vector< std::vector<int> > > 
			  &boaOut,
			  double r0=1.75 ) {
  
  if( dataIn.size()<4 ) {
    std::cerr << "removeDuplicateCells(): "
	      << "Needs intensities in position [n][3] to be able to "
	      << "calculate intesity weighted new position of cells\n";
    return -1;
  }
  
  std::vector< std::vector<int> > neigh( dataIn.size() );
  std::vector< std::vector<double> > dist( dataIn.size() );
  
  // Find neighbors for each cell (cells closer than r0)
  //////////////////////////////////////////////////////////////////////
  for( int i=0 ; i<dataIn.size() ; i++ ) {
    for( int j=0 ; j<dataIn.size() ; j++ ) {
      if( i!=j ) {
	double d=std::sqrt( double ( (dataIn[i][0]-dataIn[j][0])*
				     (dataIn[i][0]-dataIn[j][0])
				     +(dataIn[i][1]-dataIn[j][1])*
				     (dataIn[i][1]-dataIn[j][1]) ) );
	if( d<=r0 ) {
	  neigh[i].push_back( j ); 
	  dist[i].push_back( d ); 
	}
      }
    }
  }
  
  // Recursive grouping of neighbors into clusters of old cells
  //////////////////////////////////////////////////////////////////////
  std::vector< std::vector<int> > groups;
  std::vector<int> mark( dataIn.size() );
  std::vector<int> tmp(1);
  int groupNum=1;
  for( int i=0 ; i<neigh.size() ; i++ ) {
    if( !mark[i] ) { //Create new group and add i and it's neighbors
      tmp[0]=i;
      groups.push_back( tmp );
      mark[i] = groupNum;
      addNeighbors(i,mark,neigh,groups);
      groupNum++;
    }      
  }  

  //Create new cells from the clusters as a intesity-weighted average position
  //of cluster-cell positions.
  //////////////////////////////////////////////////////////////////////
  double mX,mY,weightI,weightB;
  std::vector<int> newTmp( 4 );
  dataOut.resize( groups.size(), newTmp );
  boaOut.resize( groups.size() );
  for( int g=0 ; g<groups.size() ; g++ ) {
    mX=mY=weightI=weightB=0.;
    for( int h=0 ; h<groups[g].size() ; h++ ) {
      int i = groups[g][h]; 
      weightB += dataIn[i][2];
      weightI += dataIn[i][3];
      mX += dataIn[i][0]*dataIn[i][3];
      mY += dataIn[i][1]*dataIn[i][3];
      if( h==0 ) {
	boaOut[g] = boaIn[i];
      }
      else {
	int currentSize=boaOut[g].size();
	int addSize=boaIn[i].size();
	boaOut[g].resize( currentSize+addSize );
	for( int k=0 ; k<addSize ; k++ ) {
	  boaOut[g][currentSize+k].resize(2);
	  boaOut[g][currentSize+k][0]=boaIn[i][k][0];
	  boaOut[g][currentSize+k][1]=boaIn[i][k][1];
	}
      }
    }
    if( weightI>0. && groups[g].size() ) {
      dataOut[g][0] = int( mX/weightI + 0.5 ); 
      dataOut[g][1] = int( mY/weightI + 0.5 ); 
      dataOut[g][2] = int( weightB +0.5 ); //Basin of attraction size
      dataOut[g][3] = int( weightI/groups[g].size() + 0.5 ); 
    }
    else {
      std::cerr << "removeDuplicateCells():"
	   << " New cell based on zero weighted info...Returning original!\n";
      std::cerr << g << " " << groups[g].size() << " " << weightI << "\n";
      dataOut = dataIn;
      boaOut = boaIn;
      return -1;
    }
  }
  // Statistics for the new groups /////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
//    std::cerr << dataIn.size() << " cells grouped into " << groups.size() 
//         << " cells\n\n"; 
//    std::cerr << "#Group\tnumCells\tmin(d)\t<d>\tmax(d)\n";
//    double mean,min,max;
//    int count;
//    for( int g=0 ; g<groups.size() ; g++ ) {
//      mean = 0.;
//      count = 0;
//      min=max=0.;
//      for( int h=0 ; h<groups[g].size() ; h++ ) {
//        for( int hh=h+1 ; hh<groups[g].size() ; hh++ ) {
//  	int i=groups[g][h];
//  	int j=groups[g][hh];
//  	double d=sqrt( (dataIn[i][0]-dataIn[j][0])*(dataIn[i][0]-dataIn[j][0])
//  		       +(dataIn[i][1]-dataIn[j][1])*(dataIn[i][1]-dataIn[j][1])
//  		       +(dataIn[i][2]-dataIn[j][2])*(dataIn[i][2]-dataIn[j][2])
//  		       );
//  	mean += d;
//  	count++;
//  	if( (h==0 && hh==1) || d>max )
//  	  max = d;
//  	if( (h==0 && hh==1) || d<min )
//  	  min = d;
//        }
//      }
//      if( count ) {//More than one cell which means a mean dist can be calculated
//        mean /= count;
//        std::cerr << g+1 << "\t" << groups[g].size() << "\t" << min << "\t" 
//  	   << mean << "\t" << max << "\n";
//      }
//      else {//Only one cell in the group...
//        std::cerr << g+1 << "\t" << groups[g].size() << "\t" << min << "\t" 
//  	   << mean << "\t" << max << "\n";
//      }
//    }   
  //////////////////////////////////////////////////////////////////////

  std::cerr << dataIn.size() << " cells grouped into " << groups.size() 
       << " new cells.\n";
  return dataIn.size() - groups.size();//Number of removed cells
}

//! Recursive algorithm for tracing neighbors and neighbors to neighbors, ...
void addNeighbors(int i, std::vector<int> &mark,
		  std::vector< std::vector<int> > &neigh,
		  std::vector< std::vector<int> > &groups) {
  
  for( int j=0 ; j<neigh[i].size() ; j++ ) {      
    if( !mark[ neigh[i][j] ] ) {
      mark[ neigh[i][j] ] = mark[i];
      groups[ mark[i]-1 ].push_back( neigh[i][j] );
      addNeighbors(neigh[i][j],mark,neigh,groups);
    }
    else if( mark[i] != mark[ neigh[i][j] ] ) {
      std::cerr << "addNeighbors(): " << i << " added to group "
	   << mark[i] << ", while it's neighbor " << neigh[i][j] 
	   << " is in group " << mark[ neigh[i][j] ] << ". Exiting!\n";
      exit(-1);
    }
  }
}    

//!Takes a list of pixel lists and check which are connected to which
/*! H,W is the number of rows,cols in the image. boa is a list of
  pixel positions for all the cells, walls are where lists of pixels
  for the walls will be stored, wallPixelCell saves the cell number
  for wall pixels, neigh stores the neighborhood relationships.  It is
  using the pixels marked in boa and walls and allows for an overlap,
  where ir removes the pixels from the cells. It extracts the center
  of mass positions, the volumes, and marks if the compartment is a
  cell or wall.*/
void createWallsFromBOAs(int H,int W,
			 std::vector< std::vector< std::vector<int> > > &boa,
			 std::vector< std::vector< std::vector<int> > > &walls,
			 std::vector< std::vector< std::vector<int> > > 
			 &compartmentBoa,
			 std::vector< std::vector<int> > &neigh, 
			 std::vector< std::vector<double> > &compartment, 
			 double radius) {
  
  int N = boa.size();
  // Find neighbors to each cell
  //////////////////////////////////////////////////////////////////////
  std::vector< std::vector<int> > latticeCell( H ),wallMarker( H );
  for( int i=0 ; i<H ; i++ ) {
    latticeCell[i].resize( W, -1 );
    wallMarker[i].resize( W, -2 );
  }
  //Group lattice points from basins of attraction
  for( int c=0 ; c<N ; c++ )
    for( int k=0 ; k<boa[c].size() ; k++ ) {
      int x=boa[c][k][0];
      int y=boa[c][k][1];
      if( latticeCell[x][y]>-1 ) {
	std::cerr << "Warning : lattice["<<x<<"]["<<y<<"] = "
		  <<latticeCell[x][y]
		  <<" AND " << c << " !!!\n"; 
      }
      latticeCell[x][y]=c;
    }
  
  if( !N==neigh.size() ) 
    neigh.resize( N );
  for( int i=0 ; i<N ; i++ )
    neigh[i].resize(0);
  
  //Define cell neighbors from pixel neighbors in lattice
  for( int i=1 ; i<H ; i++ ) {
    for( int j=1 ; j<W ; j++ ) {
      int c1=latticeCell[i][j];
      int c2=latticeCell[i][j-1];
      int c3=latticeCell[i-1][j];
      if( c1 != c2 && wallMarker[i][j]<-1 && wallMarker[i][j-1]<-1 ) {
	wallMarker[i][j]=c2;
	wallMarker[i][j-1]=c1;
	if( c1>-1 ) {//c1 not backround
	  int newNeigh=1;
	  for( int k=0 ; k<neigh[c1].size() ; k++ )
	    if( neigh[c1][k]==c2 )
	      newNeigh=0;
	  if( newNeigh )
	    neigh[c1].push_back(c2);
	}
	if( c2>-1 ) {//c2 not background
	  int newNeigh=1;
	  for( int k=0 ; k<neigh[c2].size() ; k++ )
	    if( neigh[c2][k]==c1 )
	      newNeigh=0;
	  if( newNeigh )
	    neigh[c2].push_back(c1);
	}
      }
      if( c1 != c3 && wallMarker[i][j]<-1 && wallMarker[i-1][j]<-1 ) {
	wallMarker[i][j]=c3;
	wallMarker[i-1][j]=c1;
	
	if( c1>-1 ) {//c1 not background
	  int newNeigh=1;
	  for( int k=0 ; k<neigh[c1].size() ; k++ )
	    if( neigh[c1][k]==c3 )
	      newNeigh=0;
	  if( newNeigh )
	    neigh[c1].push_back(c3);
	}
	if( c3>-1 ) {//c3 not background
	  int newNeigh=1;
	  for( int k=0 ; k<neigh[c3].size() ; k++ )
	    if( neigh[c3][k]==c1 )
	      newNeigh=0;
	  if( newNeigh )
	    neigh[c3].push_back(c1);
	}
      }
    }
  } 
  
  //Define the walls from the neighborhood values
  int numNeigh=0;
  for( int i=0 ; i<N ; i++ )
    for( int k=0 ; k<neigh[i].size() ; k++ ) {
      int j=neigh[i][k];
      if( i>j ) 
	numNeigh++;
    }
  
  walls.resize( 2*numNeigh );
  std::vector<int> wallI( walls.size() ),wallJ(walls.size());
  int wallCount=0;
  for( int n=0 ; n<N ; n++ ) {
    for( int k=0 ; k<neigh[n].size() ; k++ ) {
      int nn=neigh[n][k];
      if( n>nn ) {
	wallI[wallCount] = n; 
	wallJ[wallCount++] = nn; 
	wallI[wallCount] = nn; 
	wallJ[wallCount++] = n;
      }
    }
  }
  if( wallCount != walls.size() ) {
    std::cerr << "createWallsFromBOA Wrong number of defined walls\n";
    exit(-1);
  }

  //Add walldefining pixels into the walls vector...
  //////////////////////////////////////////////////////////////////////
  for( int i=0 ; i<H ; i++ )
    for( int j=0 ; j<W ; j++ )
      if( wallMarker[i][j]>-2 ) {
	int hitCounter=0;
	for( int w=0 ; w<walls.size() ; w++ )
	  if( wallI[w]==latticeCell[i][j] && wallJ[w]==wallMarker[i][j] ) {
	    hitCounter++;
	    int wallSize = walls[w].size();
	    walls[w].resize(wallSize+1);
	    walls[w][wallSize].resize(2);
	    walls[w][wallSize][0]=i;
	    walls[w][wallSize][1]=j;
	  }
	if( hitCounter!=1 ) {
	  std::cerr << "createWallsFromBOA One pixel to one wall only\n";
	  std::cerr << hitCounter << " walls added to pixel " << i << "," << j
		    << " (with cell " << latticeCell[i][j] << " and neigh "
		    << wallMarker[i][j] << ")\n"; 
	  exit(-1);
	}
	
      }
  
  //Extend walls to radius
  //////////////////////////////////////////////////////////////////////
  if( radius>1.0 ) {
    std::cerr << "createWallsFromBOA Expanding cell walls to radius "
	      << radius << "\n";
    //Create list of walls for each cell
    std::vector< std::vector<int> > cellWall( N );
    std::vector<int> bgWall;
    for( int w=0 ; w<walls.size() ; w++ ) {
      if( wallI[w]>=0 )
	cellWall[ wallI[w] ].push_back( w );
      else
	bgWall.push_back( w );
    }
    //Find closest wall and distance for each boa pixel
    std::vector< std::vector<int> > boaWall( N );
    std::vector< std::vector<double> > boaWallDistance( N );
    for( int n=0 ; n<N ; n++ ) {
      boaWall[n].resize( boa[n].size() );
      boaWallDistance[n].resize( boa[n].size() );
      for( int c=0 ; c<boa[n].size() ; c++ ) {
	//Find shortest distance to a wall
	int boaI=boa[n][c][0],boaJ=boa[n][c][1];
	int minW;
	double minWDistance;
	for( int k=0 ; k<cellWall[n].size() ; k++ ) {
	  int w=cellWall[n][k];
	  for( int wp=0 ; wp<walls[w].size() ; wp++ ) {
	    if( k==0 && wp==0 ) {
	      minW=w;
	      minWDistance = std::sqrt( (walls[w][wp][0]-boaI)*
					(walls[w][wp][0]-boaI) +
					(walls[w][wp][1]-boaJ)*
					(walls[w][wp][1]-boaJ) );
	    }
	    else {
	      double distance = std::sqrt( (walls[w][wp][0]-boaI)*
					   (walls[w][wp][0]-boaI) +
					   (walls[w][wp][1]-boaJ)*
					   (walls[w][wp][1]-boaJ) );
	      if( distance<minWDistance ) {
		minW=w;
		minWDistance=distance;
	      }
	    }
	  }
	}
	boaWall[n][c]=minW;
	boaWallDistance[n][c]=minWDistance;
      }
    }
    //Add boa pixels to walls if close enough (distance<radius)
    std::vector<int> tmpVec(2);
    for( int n=0 ; n<N ; n++ ) {
      for( int c=0 ; c<boa[n].size() ; c++ ) {
	if( boaWallDistance[n][c]<radius ) {
	  tmpVec[0]=boa[n][c][0];
	  tmpVec[1]=boa[n][c][1];
	  walls[boaWall[n][c]].push_back(tmpVec);
	  wallMarker[ boa[n][c][0] ][ boa[n][c][1] ]=wallJ[boaWall[n][c]];
	}
      }
    }
  }
  //Add walls into compartmentBoa
  //////////////////////////////////////////////////////////////////////
  std::cerr << "Adding walls into compartmentBoa.\n";
  int numCell=boa.size();
  int numWall=walls.size();
  compartmentBoa.resize(numCell+numWall);
  for( int w=0 ; w<walls.size() ; w++ )
    compartmentBoa[ N+w ] = walls[w];
  
  //Extract all compartmental properties and put it in compartments
  // and in compartmentBoa
  //////////////////////////////////////////////////////////////////////
  std::cerr << "Adding cells into compartmentBoa and cell properties "
	    << "into compartment.\n";
  compartment.resize(numCell+numWall);
  for( int i=0 ; i<compartment.size() ; i++ )
    compartment[i].resize(5);
  
  std::vector<int> tmpInt(2);
  //Get cell properties
  for( int i=0 ; i<H ; i++ ) {
    for( int j=0 ; j<W ; j++ ) {
      int cell=latticeCell[i][j];
      if( cell>=numCell ) {
	std::cerr << "createWallsFromBasinsOfAttractions: "
		  << "Cellflag larger than number of cells!\n";
	exit(-1);
      }
      if( cell>-1 && wallMarker[i][j]<-1 ) { //Included in cell
	compartment[cell][0] += double(i);
	compartment[cell][1] += double(j);
	compartment[cell][2] += 1.0;
	compartment[cell][3] = 1.0;
	compartment[cell][4] = 0.0;
	tmpInt[0]=i;
	tmpInt[1]=j;
	compartmentBoa[cell].push_back(tmpInt);
      }
    }
  }
  for( int i=0 ; i<numCell ; i++ ) {
    if( compartment[i][2]<=0.0 ) {
      std::cerr << "createWallsFromBasinsOfAttractions: "
		<< "Cell with zero volume!\n";
      exit(-1);
    }
    compartment[i][0] /= compartment[i][2];
    compartment[i][1] /= compartment[i][2];
  }
  std::cerr << "Adding wall properties into compartment.\n";
  //Get wall properties
  for( int i=0 ; i<numWall ; i++ ) {
    compartment[i+numCell][2] = double(walls[i].size());
    compartment[i+numCell][3] = 0.0;
    compartment[i+numCell][4] = 1.0;
    for( int j=0 ; j<walls[i].size() ; j++ ) {
      compartment[i+numCell][0] += double(walls[i][j][0]);
      compartment[i+numCell][1] += double(walls[i][j][1]);
    }
    
    if( compartment[i+numCell][2]<=0.0 ) {
      std::cerr << "createWallsFromBasinsOfAttractions: "
		<< "Warning: Wall with zero volume!\n";
      exit(-1);
    }
    else {
      compartment[i+numCell][0] /= compartment[i+numCell][2]; 
      compartment[i+numCell][1] /= compartment[i+numCell][2]; 
    }
  }
  
  //Extract all neighborhood relationships including sizes of connecting areas
  //////////////////////////////////////////////////////////////////////
  std::vector< std::vector<int> > compartmentNeigh; 
  std::vector< std::vector<double> > compartmentNeighSize; 
  compartmentNeigh.resize(compartment.size());
  compartmentNeighSize.resize(compartment.size());
  //Find cell-wall neighbors 
  for( int i=0 ; i<numCell ; i++ )
    for( int w=0 ; w<numWall ; w++ )
      if( wallI[w]==i ) {
	double area=compartment[w+numCell][2]/radius;
	compartmentNeigh[i].push_back(w+numCell);
	compartmentNeigh[w+numCell].push_back(i);
	compartmentNeighSize[i].push_back(area);
	compartmentNeighSize[w+numCell].push_back(area);
      }
  //Find wall-wall neighbors (assuming pairwise defined)
  for( int w=0 ; w<numWall ; w+=2 ) {
    if( !wallI[w]==wallJ[w+1] || !wallI[w]==wallJ[w+1] ) {
      std::cerr << "createWallsFromBasinsOfAttractions: "
		<< "Wrong wall neighbor relations!\n";
      exit(-1);
    }
    double area;
    if( wallI[w]>-1 && wallI[w+1]>-1 ) {//internal wall
      area=0.5*(compartment[w+numCell][2]+compartment[w+1+numCell][2])/radius;
    }
    else if( wallI[w]>-1 && wallI[w+1]==-1 ) {//outer wall
      area=0.5*(compartment[w+numCell][2]/radius+compartment[w+1+numCell][2]);
    }
    else if( wallI[w]==-1 && wallI[w+1]>-1 ) {//outer wall
      area=0.5*(compartment[w+numCell][2]+compartment[w+1+numCell][2]/radius);
    }
    else {
      std::cerr << "createWallsFromBOA: Problem defining wall-wall area\n";
      exit(-1);
    }
    compartmentNeigh[w+numCell].push_back(w+1+numCell);
    compartmentNeigh[w+1+numCell].push_back(w+numCell);
    compartmentNeighSize[w+numCell].push_back(area);
    compartmentNeighSize[w+1+numCell].push_back(area);
  }
  std::cerr << "Writing cell neighborhood in /tmp/cell.neigh\n"; 
  std::ofstream os("/tmp/cell.neigh");
  if( !os ) {
    std::cerr << "createWallsFromBasinsOfAttractions: "
	      << "Cannot open neighbor output file!\n";
    exit(-1);
  }
  os << compartmentNeigh.size() << " 1\n";
  for( int i=0 ; i<compartmentNeigh.size() ; i++ ) {
    if( compartmentNeigh.size() == 0 || 
	compartmentNeigh.size() != compartmentNeighSize.size() ) { 
      std::cerr << "createWallsFromBasinsOfAttractions: "
		<< "Wrong wall neighbors or neighbor sizes!\n";
      exit(-1);
    }    
    os << i << " " << compartmentNeigh[i].size() << " ";
    for( int j=0 ; j<compartmentNeigh[i].size() ; j++ )
      os << compartmentNeigh[i][j] << " ";
    for( int j=0 ; j<compartmentNeighSize[i].size() ; j++ )
      os << compartmentNeighSize[i][j] << " ";
    os << "\n";
  }
  os.close();
}

void writeCompartmentPixels(char* file,
			    std::vector< std::vector<double> > &compartment,
			    std::vector< std::vector< std::vector<int> > > 
			    &boa,
			    std::vector< std::vector< std::vector<int> > > 
			    &walls) {
  

  std::ofstream os2( file );
  if( !os2 ) {
    std::cerr << "writeCompartmentPixels: "
	      << "Cannot open compartment output file!\n";
    exit(-1);
  }
  int numCompartment = compartment.size();
  int numWall = walls.size();
  int numCell = boa.size(); 
  if( numCompartment != numWall+numCell ) {
    std::cerr << "writeCompartmentPixels: "
	      << "Number of compartments does not agree with number of " 
	      << "cells and walls.\n";
    exit(-1);
  }
  os2 << numCompartment << "\n";
  for( int i=0 ; i<numCell ; i++ ) {
    os2 << boa[i].size() << "\n";
    for( int j=0 ; j<boa[i].size() ; j++ )
      os2 << boa[i][j][0] << " " << boa[i][j][1] << "\n";
  }
  for( int i=0 ; i<numWall ; i++ ) {
    os2 << walls[i].size() << "\n";
    for( int j=0 ; j<walls[i].size() ; j++ )
      os2 << walls[i][j][0] << " " << walls[i][j][1] << "\n";
  }
  os2.close();
}

//! Calculates line from pixels using least square method and saves endpoints
void wallsFromPixelsToEndpoints( std::vector< std::vector< std::vector<int> > >
				 &walls,
				 std::vector< std::vector<int> > &endpoints) {

  int N=walls.size();
  endpoints.resize(2*N);
  for( int i=0 ; i<2*N ; i++ )
    endpoints[i].resize(2);

  int count=0;
  for( int i=0 ; i<N ; i++ ) {

    if( walls[i].size()<8 ) {
      std::cerr << "wallsFromPixelsToEndpoints: Too few points for line...\n";
      std::cerr << "Skipping wall number " << i << " ( " << walls[i].size() 
	<< " pixels)\n";
    }
    else {
      //Calculate least square line parameters a,b for best line (y=ax+b)
	double Sx=0.,Sy=0.,Sxx=0.,Sxy=0.;
      for( int j=0 ; j<walls[i].size() ; j++ ) {
	int x=walls[i][j][0];
	int y=walls[i][j][1];
	Sx += x;
	Sy += y;
	Sxx += x*x;
	Sxy += x*y;
      }
      double div,a,b;
      int M=walls[i].size();
      div = ( M*Sxx-Sx*Sx );
      if( div != 0. ) {
	a = ( Sxx*Sy-Sx*Sxy )/div;
	b = ( M*Sxy-Sx*Sy )/div;
      }
      else {
	std::cerr << "wallsFromPixelsToEndpoints: Bad statistics...\n";
	for( int j=0 ; j<walls[i].size() ; j++ ) 
	  std::cerr << walls[i][j][0] << " " << walls[i][j][1] << "\n";
	exit(-1);
      }
      //std::cerr << "y = " << a << " + " << b << " * x\n";
      //Find closest points to line from end pixels
	//(pixels most far away from each other)
	  double max=0.;
      int max1=0,max2=1;
      for( int j=0 ; j<walls[i].size() ; j++ )
	for( int k=j+1 ; k<walls[i].size() ; k++ ) {
	  double d = std::sqrt( double(
				       ( (walls[i][j][0]-walls[i][k][0])*
					 (walls[i][j][0]-walls[i][k][0]) ) +
				       ( (walls[i][j][1]-walls[i][k][1])*
					 (walls[i][j][1]-walls[i][k][1]) )
				       ) );
	  if( d>max ) {
	    max=d;
	    max1=j;
	    max2=k;
	  } 
	  
	}
      
      //Add the two "border" points on the line
	double aux = (walls[i][max1][0]+b*walls[i][max1][1]-a*b)/(b*b+1) ;
      endpoints[count][0] = int( aux +0.5 );
      endpoints[count][1] = int( a + b*aux +0.5 );
      //std::cerr << walls[i][max1][0] << " " << walls[i][max1][1] << " -> "; 
      //std::cerr << endpoints[count][0] << " " << endpoints[count][1] << "\n";
      count++;
      aux = (walls[i][max2][0]+b*walls[i][max2][1]-a*b)/(b*b+1) ;
      endpoints[count][0] = int( aux +0.5 );
      endpoints[count][1] = int( a + b*aux +0.5 );
      //std::cerr << walls[i][max2][0] << " " << walls[i][max2][1] << " -> "; 
      //std::cerr << endpoints[count][0] << " " << endpoints[count][1] << "\n";
      count++;
    }
  }
  if( count != 2*N ) {
    std::cerr << "wallFromPixelsToEndpoints: Fewer number of endpoints found\n";
    std::cerr << count << " " << 2*N << "\n";
    endpoints.resize(count);
  }
    
}

//!Collects wall pixels belonging to specific cells
void gatherWallsForCells( std::vector< std::vector< std::vector<int> > > 
			  &walls,
			  std::vector< std::vector<int> > &wallPixelCell,
			  std::vector< std::vector< std::vector<int> > > 
			  &cellWalls) {
  
  //Check that cellWalls is large enough...
  int maxCellIndex=0;
  for( int i=0 ; i<wallPixelCell.size() ; i++ )
    for( int j=0 ; j<wallPixelCell[i].size() ; j++ )
      if( wallPixelCell[i][j] > maxCellIndex )
	maxCellIndex=wallPixelCell[i][j];
  if( maxCellIndex >=cellWalls.size() ) 
    cellWalls.resize( maxCellIndex );

  
  for( int i=0 ; i<walls.size() ; i++ ) 
    for( int j=0 ; j<walls[i].size() ; j++ ) {
      int cellIndex=wallPixelCell[i][j];
      int cellWallSize=cellWalls[ cellIndex ].size();
      cellWalls[cellIndex].resize(cellWallSize+1);
      cellWalls[cellIndex][cellWallSize].resize(2);
      cellWalls[cellIndex][cellWallSize][0]=walls[i][j][0];
      cellWalls[cellIndex][cellWallSize][1]=walls[i][j][1];
    }
}

//!Calculates an average position from a list of pixels eg basin of attraction
void cellCenterFromPixels( std::vector< std::vector< std::vector<int> > > 
			   &pixels,
			   std::vector< std::vector<int> > &cellCenter) {

  if( pixels.size() != cellCenter.size() ) {
    std::cerr << "cellCenterFromPixels Warning changing size on cellCenter\n";
    cellCenter.resize( pixels.size() );
  }
  for( int i=0 ; i<pixels.size() ; i++ )
    if( cellCenter[i].size() != 2 )
      cellCenter[i].resize(2);
  
  for( int i=0 ; i<pixels.size() ; i++ ) {
    double  mean0=0.,mean1=0.;
    for( int j=0 ; j<pixels[i].size() ; j++ ) {
      if( pixels[i][j].size() != 2 ) {
	std::cerr << "cellCenterFromPixels wrong size of pixels positions\n";
	exit(-1);
      }

      mean0 += pixels[i][j][0];
      mean1 += pixels[i][j][1];
    }
    mean0 /= pixels[i].size();
    mean1 /= pixels[i].size();
    cellCenter[i][0] = int( mean0 + 0.5 );
    cellCenter[i][1] = int( mean1 + 0.5 );
  }
}

//!Calculates "polarization" wrt cellCenter of given pixel positions
void calculatePolarization( std::vector< std::vector< std::vector<int> > > 
			    &pixels,
			    std::vector< std::vector<int> > &cellCenter,
			    std::vector< std::vector<double> > &polarization) {
  
  int N=pixels.size();
  if( cellCenter.size() != N || polarization.size() != N ) {
    std::cerr << "calculatePolarization Wrong size of given std::vectors.\n";
    std::cerr << N << " " << cellCenter.size() << " " 
	 << polarization.size() << "\n";     
    exit(-1);
  }

  for( int i=0 ; i<N ; i++ ) {
    double p0=0.;
    double p1=0.;
    for( int j=0 ; j<pixels[i].size() ; j++ ) {
      double r0=pixels[i][j][0]-cellCenter[i][0];
      double r1=pixels[i][j][1]-cellCenter[i][1];
      double norm = std::sqrt( r0*r0+r1*r1 );
      if( norm>0. ) {
	r0 /= norm;
	r1 /= norm;
      }
      p0 += r0;
      p1 += r1;
    }
    polarization[i][0] = p0;
    polarization[i][1] = p1;
    polarization[i][2] = sqrt( p0*p0+p1*p1 );
  }
}

//!Reads pixel positions from a file and saves them in pixels
void readPixelPos( char *file, 
		   std::vector< std::vector<int> > &pixels ) {
  
  std::ifstream IN( file );
  if( !IN ) {
    std::cerr << "readPixelPos::Cannot open datafile " << file 
	 << "\n\n\7";exit(-1);}
  
  //Read number of pixels 
  int numPixel,numCol,tmp;
  IN >> numPixel;
  pixels.resize( numPixel );
  IN >> numCol;

  
  for( int i=0 ; i<numPixel ; i++ ) {
    pixels[i].resize(2);
    IN >> pixels[i][0];
    IN >> pixels[i][1];
    for( int j=2 ; j<numCol ; j++ )
      IN >> tmp;
  }
  IN.close();
}

//!Calculates wall polarization in IA normalized with IB
void printWallPolarization( Image &IA, Image &IB,
			    std::vector< std::vector< 
			    std::vector<int> > > &walls,
			    std::vector< std::vector<int> >
			    & wallPixelCell) {
  
  


  //Set x,y as mean row,col position from the wall
  for( int i=0 ; i<walls.size() ; i++ ) {
    int boa=-1;
    int N1=0,N2=0;
    int x1=0,x2=0,y1=0,y2=0;
    double A1=0.0,A2=0.0,B1=0.0,B2=0.0;
    for( int j=0 ; j<walls[i].size() ; j++ ) {
      if( j==0 ) boa=wallPixelCell[i][j];
      
      if( wallPixelCell[i][j]==boa ) {
	N1++;
	x1 += walls[i][j][0];
	y1 += walls[i][j][1];
	A1 += IA.pix(walls[i][j][0],walls[i][j][1]); 
	B1 += IB.pix(walls[i][j][0],walls[i][j][1]); 
      }
      else {
	N2++;
	x2 += walls[i][j][0];
	y2 += walls[i][j][1];
	A2 += IA.pix(walls[i][j][0],walls[i][j][1]); 
	B2 += IB.pix(walls[i][j][0],walls[i][j][1]); 	
      }
    }
    //normalize
    //A1 /= B1;
    //A2 /= B2;
    
    int x= (x1+x2)/(N1+N2);
    int y= (y1+y2)/(N1+N2);
    
    double dx=x2/N2-x1/N1;
    double dy=y2/N2-y1/N1;
    double norm = std::sqrt(dx*dx+dy*dy);
    dx /= norm;
    dy /= norm;
    
    double F=(A1-A2);//transport from higher to lower

    std::cout << x << " " << y << " " << F*dx << " " << F*dy << " " 
	      << std::fabs(F) << " " << std::fabs(F) << "\n"; 

  }
}

void getCompartmentConcentrations( Image &I,
				   std::vector< std::vector< 
				   std::vector<int> > > &compartmentBoa,
				   std::vector<double> &compartmentIntensity) {
    
  int N=compartmentBoa.size();//number of compartments
  compartmentIntensity.resize( N ,0.0);
  
  //Get compartment intensities
  for( int i=0 ; i< N ; i++ ) {
    for( int k=0 ; k<compartmentBoa[i].size() ; k++ ) {
      compartmentIntensity[i] += I.pix(compartmentBoa[i][k][0],
				       compartmentBoa[i][k][1]);
    }
    if( compartmentIntensity[i]<=0 )
      compartmentIntensity[i]=0;
    else
      compartmentIntensity[i]/=compartmentBoa[i].size();
  }

  //Normalize to max=1;
  double max=0.0;
  for( int i=0 ; i< N ; i++ )
    if( compartmentIntensity[i]>max )
      max = compartmentIntensity[i];
  
  if( max<=0.0 ) {
    std::cerr << "getWallAndCellConcentrations No intensities in compartments"
	      << ".\n";
    exit(-1);
  }
  for( int i=0 ; i< N ; i++ )
    compartmentIntensity[i]/=max;

  return;
}

//!Creates neighbor relations and cross section areas from basin of attractors
void createNeighborsFromBOAs(int H,int W,
			     std::vector< std::vector< std::vector<int> > > 
			     &boa,
			     std::vector< std::vector< std::vector<int> > > 
			     &compartmentBoa,
			     std::vector< std::vector<double> > &compartment) {

  int N = boa.size();
  std::vector< std::vector<int> > neigh(N),neighSize(N);
  std::vector<int> border(N);//Marks wether a cell is neighbor to background
  // Find neighbors to each cell
  //////////////////////////////////////////////////////////////////////
  std::vector< std::vector<int> > latticeCell( H ),wallMarker( H );
  for( int i=0 ; i<H ; i++ ) {
    latticeCell[i].resize( W, -1 );
    wallMarker[i].resize( W, -2 );
  }
  //Group lattice points from basins of attraction
  for( int c=0 ; c<N ; c++ )
    for( int k=0 ; k<boa[c].size() ; k++ ) {
      int x=boa[c][k][0];
      int y=boa[c][k][1];
      if( latticeCell[x][y]>-1 ) {
	std::cerr << "Warning : lattice["<<x<<"]["<<y<<"] = "
		  <<latticeCell[x][y]
		  <<" AND " << c << " !!!\n"; 
      }
      latticeCell[x][y]=c;
    }
  
  //Define cell neighbors from pixel neighbors in lattice
  for( int i=1 ; i<H ; i++ ) {
    for( int j=1 ; j<W ; j++ ) {
      int c1=latticeCell[i][j];
      int c2=latticeCell[i][j-1];
      int c3=latticeCell[i-1][j];
      if( c1 != c2 && wallMarker[i][j]<-1 && wallMarker[i][j-1]<-1 ) {
	wallMarker[i][j]=c2;
	wallMarker[i][j-1]=c1;
	if( c1>-1 && c2>-1 ) {//c1 and c2 not backround pixels
	  //Add c2 as c1 neighbor
	  int newNeigh=1;
	  for( int k=0 ; k<neigh[c1].size() ; k++ )
	    if( neigh[c1][k]==c2 ) {
	      newNeigh=0;
	      neighSize[c1][k]++;
	    }
	  if( newNeigh ) {
	    neigh[c1].push_back(c2);
	    neighSize[c1].push_back(1);
	  }
	  //Add c1 as c2 neighbor
	  newNeigh=1;
	  for( int k=0 ; k<neigh[c2].size() ; k++ )
	    if( neigh[c2][k]==c1 ) {
	      newNeigh=0;
	      neighSize[c2][k]++;
	    }
	  if( newNeigh ) {
	    neigh[c2].push_back(c1);
	    neighSize[c2].push_back(c2);
	  }
	}
      }
      else {
	if( c1<0 ) border[c2]=1;
	if( c2<0 ) border[c1]=1;
      }
      if( c1 != c3 && wallMarker[i][j]<-1 && wallMarker[i-1][j]<-1 ) {
	wallMarker[i][j]=c3;
	wallMarker[i-1][j]=c1;
	
	if( c1>-1 && c3>-1 ) {//c1 and c3 not background pixels
	  //Add c3 as c1 neighbor
	  int newNeigh=1;
	  for( int k=0 ; k<neigh[c1].size() ; k++ )
	    if( neigh[c1][k]==c3 ) {
	      newNeigh=0;
	      neighSize[c1][k]++;
	    }
	  if( newNeigh ) {
	    neigh[c1].push_back(c3);
	    neighSize[c1].push_back(1);
	  }
	  //Add c1 as c3 neighbor
	  newNeigh=1;
	  for( int k=0 ; k<neigh[c3].size() ; k++ )
	    if( neigh[c3][k]==c1 ) {
	      newNeigh=0;
	      neighSize[c3][k]++;
	    }
	  if( newNeigh ) {
	    neigh[c3].push_back(c1);
	    neighSize[c3].push_back(1);
	  }
	}
      }
      else {
	if( c1<0 ) border[c3]=1;
	if( c3<0 ) border[c1]=1;
      }
    }
  } 
  //Move things into compartment and compertmentBoa
  compartmentBoa=boa;
  compartment.resize( boa.size() );
  for( int i=0 ; i<compartment.size() ; i++ ) {
    compartment[i].resize(4);
    double xTmp=0.0,yTmp=0.0,sizeTmp=boa[i].size();
    if( sizeTmp<=0.0 ) {
      std::cerr << "Cell of zero volume, exiting...\n";
      exit(-1);
    }
    for( int j=0 ; j<boa[i].size() ; j++ ) {
      xTmp += boa[i][j][0];
      yTmp += boa[i][j][1];
    }
    xTmp /= sizeTmp;
    yTmp /= sizeTmp;
    compartment[i][0] = xTmp;
    compartment[i][1] = yTmp;
    compartment[i][2] = sizeTmp;
    compartment[i][3] = border[i];
  }

  //Print compertment neighbor info
  //////////////////////////////////////////////////////////////////////
  std::cerr << "Writing cell neighborhood in /tmp/cell.neigh\n"; 
  std::ofstream os("/tmp/cell.neigh");
  if( !os ) {
    std::cerr << "createNeighborsFromBOAs: "
	      << "Cannot open neighbor output file!\n";
    exit(-1);
  }
  os << neigh.size() << " 1\n";
  for( int i=0 ; i<neigh.size() ; i++ ) {
    if( neigh.size() == 0 || neigh.size() != neighSize.size() ) { 
      std::cerr << "createNeighborsFromBOAs: "
		<< "Wrong wall neighbors or neighbor sizes!\n";
      exit(-1);
    }    
    os << i << " " << neigh[i].size() << " ";
    for( int j=0 ; j<neigh[i].size() ; j++ )
      os << neigh[i][j] << " ";
    for( int j=0 ; j<neighSize[i].size() ; j++ )
      os << neighSize[i][j] << " ";
    os << "\n";
  }
  os.close();
  
}
