/**
 * Filename     : findCellCenters3D.cc
 * Description  : Finds cell centers in a stack of images
 *              : Prints cell positions to stdout and plots cell positions
 *              : in original stack in output directory.
 * Author(s)    : Henrik Jonsson (henrik@caltech.edu)
 * Organization : California Institute of Technology
 * Created      : January 2003
 * Revision     : $id$
 *
 * Copyright 2003, California Institute of Technology.
 * ALL RIGHTS RESERVED.  U.S. Government Sponsorship acknowledged.
 */
#include"stack.h"
#include<iostream>

int simpleRemoveDuplicateCells( std::vector< std::vector<int> > &data, 
				double r0=1.75 );

int main( int argc,char* argv[] ) {
  
  if( argc<5 || argc>11 ) {
    std::cerr << "Usage: " << argv[0] << " inDirectory outDirectory xyScale "
	      << "zScale " << "[smothenRadius] [imageVersionFlag] "
	      << "[smothenWeight] [smothenNum]" 
	      << " [numThreshold] [intensityThreshold]\n"; 
    exit(-1);
  }
  double weight=1.;
  int numSmothen=1;
  double radius=1.;
  int imageFlag=0;
  double xyScale = atof( argv[3] );
  double zScale = atof( argv[4] );

  int numThreshold=1;
  double intensityThreshold=0.;
  if( argc>5 )
    radius = atof( argv[5] );
  if( argc>6 )
    imageFlag = atoi( argv[6] );
  if( argc>7 )
    weight = atof( argv[7] );
  if( argc>8 )
    numSmothen = atoi( argv[8] );
  if( argc>9 )
    numThreshold = atoi( argv[9] );
  if( argc>10 )
    intensityThreshold = atof( argv[10] );
  
  Stack S( argv[1],zScale,xyScale,xyScale);
  //Stack SOrig( argv[1] );
	
  //Smothening of the stack (or spherical template method?)
  if( weight>=0 && numSmothen>0 ) {

    if( imageFlag ) {
      std::cerr << "Smothening stack " << numSmothen << " time(s) using "
		<< "imageSmothening with weight " 
		<< weight << " and radius " << radius << "\n";
      for( int i=0 ; i<numSmothen ; i++ )
	S.imageSmothen(weight,radius);
    }
    else {
      std::cerr << "Smothening stack " << numSmothen << " time(s) using "
		<< "normal smothening with weight " 
		<< weight << " and radius " << radius << "\n";
      for( int i=0 ; i<numSmothen ; i++ )
	S.smothen(weight,radius);
    }
    std::cerr << "done!\n";
  }
  else
    std::cerr << "No smothening of stack...";

  std::vector< std::vector<int> > cellCenter;
  std::vector< std::vector< std::vector<int> > > flag;
  std::vector<int> tmp(4);
  
  S.findMaximaByLocalSearch(cellCenter,flag,intensityThreshold,1,numThreshold);
  std::cerr << cellCenter.size() << " cells found in stack\n"; 

  // Reduce cells to be those in SAM (Specific for each stack) 
  //////////////////////////////////////////////////////////////////////
  //std::vector< std::vector<int> > cellCenterSAM;
  //double x0=97,y0=97,z0=13,r0=35, z0Hat=18;
  //for( int n=0 ; n<cellCenter.size() ; n++ )
  //  if( cellCenter[n][0]>=z0Hat && 
  //	sqrt( (cellCenter[n][0]-z0)*(cellCenter[n][0]-z0)
  //	      +(cellCenter[n][1]-x0)*(cellCenter[n][1]-x0)
  //	      +(cellCenter[n][2]-y0)*(cellCenter[n][2]-y0) ) <r0 )
  //   cellCenterSAM.push_back( cellCenter[n] );
  //cellCenter=cellCenterSAM;
  //std::cerr << cellCenter.size() << " cells found in SAM\n"; 

  
  //simpleRemoveDuplicateCells( cellCenter );
  //while( simpleRemoveDuplicateCells( cellCenter ) );
  //while( simpleRemoveDuplicateCells( cellCenter,1.75*2.5 ) );

  //std::vector< std::vector<int> > cellCenterAdjusted;
  //S.adjustCenters( cellCenter , cellCenterAdjusted , 2. );
  //S.writeStackTiffWithRedBluePixels(argv[2],cellCenter,cellCenterAdjusted);

  //Get the basins of attraction from the flag data
  std::vector< std::vector< std::vector<int> > > boa;
  S.flagToBoa(flag,boa);

  if( boa.size() != cellCenter.size() ) {
    std::cerr << "Wrong number of attractor centers or basins!\n";
    exit(-1);
  }

  S.writeStackTiffWithRandomColors(argv[2],boa);
  //S.writeStackTiffWithRedPixels(argv[2],cellCenter);
  
  //print x y z basinOfAttraction intensity
//   std::cout << cellCenter.size() << " " << cellCenter[0].size()+1 << "\n";
//   for( int n=0 ; n<cellCenter.size() ; n++ ) {
//     std::cout << S.xScale()*cellCenter[n][1] << " " 
// 	 << S.yScale()*cellCenter[n][2] << " " 
//   	 << S.zScale()*cellCenter[n][0] << " " << cellCenter[n][3] << " ";
//     std::cout << S.pix(cellCenter[n][0],cellCenter[n][1],cellCenter[n][2]) 
//  << "\n";
//   }
}


//! Remove cells that are closer than r0 
int simpleRemoveDuplicateCells( std::vector< std::vector<int> > &data, 
				double r0 ) {
  
  std::vector< std::vector<int> > stat( data.size() );
  std::vector< std::vector<double> > dist( data.size() );
  
  for( int i=0 ; i<data.size() ; i++ ) {
    for( int j=0 ; j<data.size() ; j++ ) {
      if( i != j ) {
	double d=sqrt( (data[i][0]-data[j][0])*(data[i][0]-data[j][0])
		       + (data[i][1]-data[j][1])*(data[i][1]-data[j][1])
		       + (data[i][2]-data[j][2])*(data[i][2]-data[j][2]) );
	if( d<=r0 ) {
	  stat[i].push_back( j ); 
	  dist[i].push_back( d ); 
	}
      }
    }
  }
  
  std::vector< std::vector<int> > saves;
  std::vector<int> mark( data.size() );
  //First save all singles (and look for max)
  int max=0;
  for( int i=0 ; i<stat.size() ; i++ ) {
    if( !stat[i].size() ) {
      saves.push_back( data[i] );
      mark[i]++;
    }
    else if( stat[i].size()>max )
      max=stat[i].size();
  }
//    if( max>2 ) {
//      std::cerr << "simpleRemoveDuplicateCells:: exiting!\n"
//  	 << "Too many cells in same region...\n";
  //exit(-1);
//    }
  
  // Then go from max to lesser num of neighbors
  for( int num=max ; num>0 ; num-- ) {
    for( int i=0 ; i<stat.size() ; i++ ) {
      if( !mark[i] ) {
	double max = data[i][3];//go for highest intensity
	int keep=i;
	mark[i]++;
	for( int j=0 ; j<stat[i].size() ; j++ ) {
	  mark[stat[i][j]]++;
	  if( data[ stat[i][j] ][3]>max ) {
	    max = data[ stat[i][j] ][3];
	    keep=stat[i][j];
	  }
	}
	saves.push_back( data[keep] );
      }
    }
  }

  int numCells = data.size()-saves.size();
  if( numCells<0 ) {
    std::cerr << "simpleRemoveDuplicateCells\n"
	 << "exiting! wrong number of removed cells\n";
    exit(-1);
  }
  std::cerr << numCells << " cells removed\n"; 
  data = saves;
  
  return numCells;
  //  for( int i=0 ; i<stat.size() ; i++ )
//      if( stat[i].size() ) {
//        std::cout << i << " " << stat[i].size() << "\t";
//        for( int j=0 ; j<stat[i].size() ; j++ )
//  	std::cout << stat[i][j] << "(" << dist[i][j] << ") ";
//        std::cout << "\n";
//      }
}























