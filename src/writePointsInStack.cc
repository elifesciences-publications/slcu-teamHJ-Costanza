/**
 * Filename     : writePointsInStack.cc
 * Description  : Writes given points in a stack of data
 * Author(s)    : Henrik Jonsson (henrik@caltech.edu)
 * Organization : California Institute of Technology
 * Created      : January 2003
 * Revision     : $id$
 *
 * Copyright 2003, California Institute of Technology.
 * ALL RIGHTS RESERVED.  U.S. Government Sponsorship acknowledged.
 */
#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include "stack.h"
#include "data.h"

int main( int argc,char* argv[] ) {
  
  if( argc<6 || argc>7 ) {
    cerr << "Usage: " << argv[0] << " zScale xyScale stackDirectory "
	 << "outDirectory dataFile1 [dataFile2]\n";
    exit(-1);
  }
  double zScale=atof(argv[1]);
  double xyScale=atof(argv[2]);
  Stack S( argv[3], zScale,xyScale,xyScale );
  
  vector< vector<double> > data1,data2;
  //Read in template file and the data file
  if( readData(argv[5],data1) ) {
    cerr << "Problem reading data file " << argv[5] << "\n";
    exit(-1);
  }
  if( argc>6 ) {
    if( readData(argv[6],data2) ) {
      cerr << "Problem reading data file " << argv[6] << "\n";
      exit(-1);
    }
  }
  
  vector< vector<int> > data1Int;
  convertData(data1,data1Int,zScale,xyScale);
  if( argc==6 )
    S.writeStackTiffWithRedPixels(argv[4],data1Int);
  else {
    vector< vector<int> > data2Int;
    convertData(data2,data2Int,zScale,xyScale);
    S.writeStackTiffWithRedBluePixels(argv[4],data1Int,data2Int);
  }
}

