using namespace std;
#include"data.h"
#include<iostream>
#include<fstream>
#include<cmath>

//!Reads the position data (plus extra columns) and stores the values
/*!The data should be stored as a file with the first line indicating
  number of rows, columns. Then each row represents
  x,y,z,... values. The data is stored as z,x,y,... in the data
  vector, which is the same as for the stack. It is assumed that the
  data is stored in unit micrometer.*/
int readData( char *file,vector< vector<double> > & data ) {
  
  ifstream is( file );
  if( !is ) {
    cerr << "readData::Cannot open datafile " << file << "\n\n\7";exit(-1);}
  
  //Read in size of data matrix
  int numCells=0,numCols=0;
  is >> numCells;
  is >> numCols;

  if( !numCells || numCols<3 ) {
    cerr << "readDataTo few columns in datafile " << file << "\n\n\7";
    exit(-1);}
  
  data.resize( numCells );
  double tmp;
  for( int i=0 ; i<numCells ; i++ ) {
    data[i].resize(numCols);
    is >> data[i][1];
    is >> data[i][2];
    is >> data[i][0];
    
    //Read the rest of the columns 
    if( numCols>3 )
      for( int j=3 ; j<numCols ; j++ )
	is >> data[i][j];
  }
  is.close();
  
  return 0;
}

int readDataXYZ( char *file,vector< vector<double> > & data ) {
  
  ifstream is( file );
  if( !is ) {
    cerr << "readData::Cannot open datafile " << file << "\n\n\7";exit(-1);}
  
  //Read in size of data matrix
  int numCells=0,numCols=0;
  is >> numCells;
  is >> numCols;

  if( !numCells || numCols<3 ) {
    cerr << "readDataTo few columns in datafile " << file << "\n\n\7";
    exit(-1);}
  
  data.resize( numCells );
  double tmp;
  for( int i=0 ; i<numCells ; i++ ) {
    data[i].resize(numCols);
    is >> data[i][0];
    is >> data[i][1];
    is >> data[i][2];
    
    //Read the rest of the columns 
    if( numCols>3 )
      for( int j=3 ; j<numCols ; j++ )
	is >> data[i][j];
  }
  is.close();
  
  return 0;
}

//Read position and radii data from file
int readDataXYR( char *file,vector< vector<double> > & centers, 
		 vector<double> &radii ) {
  
  ifstream is( file );
  if( !is ) {
    cerr << "readDataXYR::Cannot open datafile " << file << "\n\n\7";exit(-1);}
  
  //Read in size of data matrix
  int numCells=0,numCols=0;
  is >> numCells;
  is >> numCols;
  
  if( !numCells || numCols<3 ) {
    cerr << "readDataXYR : To few columns in datafile " << file << "\n\n\7";
    exit(-1);}
  
  centers.resize( numCells );
  radii.resize( numCells );
  double tmp;
  for( int i=0 ; i<numCells ; i++ ) {
    centers[i].resize(numCols-1);
    is >> centers[i][0];
    is >> centers[i][1];
    is >> radii[i];
    
    //Read the rest of the columns 
    if( numCols>3 )
      for( int j=3 ; j<numCols ; j++ )
	is >> centers[i][j-1];
  }
  is.close();
  
  return 0;
}

int readDataXY( char *file,vector< vector<double> > & data ) {
  
  ifstream is( file );
  if( !is ) {
    cerr << "readData::Cannot open datafile " << file << "\n\n\7";exit(-1);}
  
  //Read in size of data matrix
  int numCells=0,numCols=0;
  is >> numCells;
  is >> numCols;
  
  if( !numCells || numCols<2 ) {
    cerr << "readDataTo few columns in datafile " << file << "\n\n\7";
    exit(-1);}
  
  data.resize( numCells );
  double tmp;
  for( int i=0 ; i<numCells ; i++ ) {
    data[i].resize(numCols);
    is >> data[i][0];
    is >> data[i][1];
    
    //Read the rest of the columns 
    if( numCols>2 )
      for( int j=2 ; j<numCols ; j++ )
	is >> data[i][j];
  }
  is.close();
  
  return 0;
}

//!Converts from micrometer-positions to pixel-positions
void convertData( vector< vector<double> > &inData,
		  vector< vector<int> > &outData, double zScale,
		  double xyScale ) {

  outData.resize( inData.size() );
  for( int i=0 ; i<inData.size() ; i++ ) {
    outData[i].resize( inData[i].size() );
    outData[i][0] = int( inData[i][0]/zScale + 0.5 );
    outData[i][1] = int( inData[i][1]/xyScale + 0.5 );
    outData[i][2] = int( inData[i][2]/xyScale + 0.5 );
    for( int j=3 ; j<inData[i].size() ; j++ )
      outData[i][j] = int( inData[i][j]+0.5 );
  }
}

//!Converts from pixeldata to micrometerdata for printing...
void convertData( vector< vector<int> > &inData,
		  vector< vector<double> > &outData, double zScale,
		  double xyScale ) {

  outData.resize( inData.size() );
  for( int i=0 ; i<inData.size() ; i++ ) {
    outData[i].resize( inData[i].size() );
    outData[i][0] = inData[i][0]*zScale;
    outData[i][1] = inData[i][1]*xyScale;
    outData[i][2] = inData[i][2]*xyScale;
    for( int j=3 ; j<inData[i].size() ; j++ )
      outData[i][j] = inData[i][j];
  }  
}

//!Converts from micrometer-positions to pixel-positions, and also zxy to xy
void convertDataToImage( vector< vector<double> > &inData,
			 vector< vector<int> > &outData,
			 double xyScale ) {

  outData.resize( inData.size() );
  for( int i=0 ; i<inData.size() ; i++ ) {
    outData[i].resize( inData[i].size()-1 );
    outData[i][0] = int( inData[i][1]/xyScale + 0.5 );
    outData[i][1] = int( inData[i][2]/xyScale + 0.5 );
    for( int j=3 ; j<inData[i].size() ; j++ )
      outData[i][j-1] = int( inData[i][j]+0.5 );
  }
}

//!Converts from micrometer-positions to pixel-positions, using only xy
void convertDataToImageXY( vector< vector<double> > &inData,
			 vector< vector<int> > &outData,
			 double xyScale ) {

  outData.resize( inData.size() );
  for( int i=0 ; i<inData.size() ; i++ ) {
    outData[i].resize( inData[i].size() );
    outData[i][0] = int( inData[i][0]/xyScale + 0.5 );
    outData[i][1] = int( inData[i][1]/xyScale + 0.5 );
    for( int j=2 ; j<inData[i].size() ; j++ )
      outData[i][j] = int( inData[i][j]+0.5 );
  }
}

//!Read 12 parameters for an affine transformation 
int readPara( char *file,vector<double> & para ) {
  
  int numOfPara=12;
  para.resize(numOfPara);
  
  ifstream is( file );
  if( !is ) {
    cerr << "readPara::Cannot open parafile " << file << "\n\n\7";exit(-1);}
  
  for( int i=0 ; i<numOfPara ; i++ )
    is >> para[i];
  
  is.close();
  return 0;
}

//!Read a matrix for a transformation 
int readMatrix( char *file,vector<double> & matrix ) {
  
  int numOfElements=16;
  matrix.resize(numOfElements);
  
  ifstream is( file );
  if( !is ) {
    cerr << "readPara::Cannot open matrixfile " << file << "\n\n\7";exit(-1);}
  
  for( int i=0 ; i<numOfElements ; i++ )
    is >> matrix[i];
  
  is.close();
  return 0;
}  
//!Transform the data from inData to outData using parameters in para
void affineTransformation( vector< vector<double> > &inData,
			   vector< vector<double> > &outData,
			   vector<double> &para ) {
  
  double PI=3.14159;
  double fac = 2.*PI/360.; 
  double cosX=cos( para[0]*fac );
  double sinX=sin( para[0]*fac );
  double cosY=cos( para[1]*fac );
  double sinY=sin( para[1]*fac );
  double cosZ=cos( para[2]*fac );
  double sinZ=sin( para[2]*fac );
  
  //Creating the transformation matrix
  vector<double> A( 12 );
  A[0] = para[6]*cosY*(cosZ+para[10]*sinZ);
  A[1] = para[7]*(cosY*(sinZ+para[9]*para[11]*cosZ)-para[11]*sinY);
  A[2] = para[8]*(para[9]*cosY*cosZ-sinY);
  A[3] = para[3];
  
  A[4] = para[6]*( sinX*sinY*(cosZ+para[10]*sinZ) - 
		   cosX*(sinZ+para[10]*cosZ) );
  A[5] = para[7]*( sinX*sinY*(sinZ+para[9]*para[10]*cosZ) + 
	      cosX*(cosZ-para[9]*para[11]*sinZ) + para[11]*sinX*cosY );
  A[6] = para[8]*( sinX*cosY + para[9]*(sinX*sinY*cosZ-cosX*sinZ) );
  A[7] = para[4]; 
  
  A[8] = para[6]*( cosX*sinY*(cosZ+para[10]*sinZ) + 
		   sinX*(sinZ-para[10]*cosZ) );
  A[9] = para[7]*( cosX*sinY*(sinZ+para[9]*para[11]*cosZ) - 
	      sinX*(cosZ-para[9]*para[11]*sinZ) + para[11]*cosX*cosY );
  A[10] = para[8]*( cosX*cosY + para[9]*(cosX*sinY*cosZ+sinX*sinZ) );
  A[11] = para[5]; 
  
  outData.resize( inData.size() );
  for( int i=0 ; i<inData.size() ; i++ ) {
    outData[i].resize( inData[i].size() );
    //X
    outData[i][0] = A[0]*inData[i][0] + A[1]*inData[i][1] +
      A[2]*inData[i][2] + A[3]; 
    //Y
    outData[i][1] = A[4]*inData[i][0] + A[5]*inData[i][1] +
      A[6]*inData[i][2] + A[7]; 
    //Z
    outData[i][2] = A[8]*inData[i][0] + A[9]*inData[i][1] +
      A[10]*inData[i][2] + A[11]; 
    //copy the rest of the data...
    for( int j=3 ; j<inData[i].size() ; j++ )
      outData[i][j] = inData[i][j];
  }
}
//!Transform the affine parameters into a transformation matrix
void affineToMatrix( vector<double> &para,vector<double> &A ) {
  
  double PI=3.14159;
  double fac = 2.*PI/360.; 
  double cosX=cos( para[0]*fac );
  double sinX=sin( para[0]*fac );
  double cosY=cos( para[1]*fac );
  double sinY=sin( para[1]*fac );
  double cosZ=cos( para[2]*fac );
  double sinZ=sin( para[2]*fac );
  
  //Creating the transformation matrix
  A.resize( 16 );
  A[0] = para[6]*cosY*(cosZ+para[10]*sinZ);
  A[1] = para[7]*(cosY*(sinZ+para[9]*para[11]*cosZ)-para[11]*sinY);
  A[2] = para[8]*(para[9]*cosY*cosZ-sinY);
  A[3] = para[3];
  
  A[4] = para[6]*( sinX*sinY*(cosZ+para[10]*sinZ) - 
		   cosX*(sinZ+para[10]*cosZ) );
  A[5] = para[7]*( sinX*sinY*(sinZ+para[9]*para[10]*cosZ) + 
		   cosX*(cosZ-para[9]*para[11]*sinZ) + para[11]*sinX*cosY );
  A[6] = para[8]*( sinX*cosY + para[9]*(sinX*sinY*cosZ-cosX*sinZ) );
  A[7] = para[4]; 
  
  A[8] = para[6]*( cosX*sinY*(cosZ+para[10]*sinZ) + 
		   sinX*(sinZ-para[10]*cosZ) );
  A[9] = para[7]*( cosX*sinY*(sinZ+para[9]*para[11]*cosZ) - 
		   sinX*(cosZ-para[9]*para[11]*sinZ) + para[11]*cosX*cosY );
  A[10] = para[8]*( cosX*cosY + para[9]*(cosX*sinY*cosZ+sinX*sinZ) );
  A[11] = para[5]; 
  
  A[12] = 0.;  
  A[13] = 0.;
  A[14] = 0.;
  A[15] = 1.;
}

//!Transform the data from inData to outData using the Matrix A
void transformation( vector< vector<double> > &inData,
		     vector< vector<double> > &outData,
		     vector<double> &A ) {
  
  outData.resize( inData.size() );
  for( int i=0 ; i<inData.size() ; i++ ) {
    outData[i].resize( inData[i].size() );
    //X
    outData[i][0] = A[0]*inData[i][0] + A[1]*inData[i][1] +
      A[2]*inData[i][2] + A[3]; 
    //Y
    outData[i][1] = A[4]*inData[i][0] + A[5]*inData[i][1] +
      A[6]*inData[i][2] + A[7]; 
    //Z
    outData[i][2] = A[8]*inData[i][0] + A[9]*inData[i][1] +
      A[10]*inData[i][2] + A[11]; 
    //copy the rest of the data...
    for( int j=3 ; j<inData[i].size() ; j++ )
      outData[i][j] = inData[i][j];
  }
}

