/**
 * Filename     : stack.cc
 * Description  : Functions for the Stack Class defined in stack.h
 * Author(s)    : Henrik Jonsson (henrik@caltech.edu)
 * Organization : California Institute of Technology
 * Created      : January 2003
 * Revision     : $id$
 *
 * Copyright 2003, California Institute of Technology.
 * ALL RIGHTS RESERVED.  U.S. Government Sponsorship acknowledged.
 */
#include"stack.h"
#include<sstream>
#include <sys/types.h>
#include <dirent.h>
#include<vector>

Stack::Stack(double zScale,double xScale,double yScale) {
  scale_.resize(3);
  setZScale(zScale);
  setXScale(xScale);
  setYScale(yScale);
}

Stack::Stack(const Stack & stackCopy) {

  std::cerr << "Stack::Stack(const Stack & stackCopy) not yet defined\n";
  //    setH( stackCopy.H() );
  //    setW( stackCopy.W() );
  //    for(int i=0 ; i<H() ; i++ )
  //      for(int j=0 ; j<W() ; j++ )
  //        setPix(i,j,stackCopy.pix(i,j));
  //    scale_.resize(3);
  //    setZScale( stackCopy.zScale() );
  //    setXScale( stackCopy.xScale() );
  //    setYScale( stackCopy.yScale() );
  //    setId( stackCopy.id() );
}

Stack::Stack( std::vector< std::vector< std::vector<double> > > & pixValue,
	      double zScale,double xScale,double yScale ) {
  
  if( pixValue.size() < 1 ) {
    std::cerr << "Stack::Stack(std::vector< std::vector< std::vector<double> > > & pixValue)\n"
	 << "Wrong dimension of lattice...\n";
    exit(-1);
  }
    
  Image tmp( pixValue[0] );
  //tmp.setH( pixValue[0].size() );
  //tmp.setW( pixValue[0][0].size() );
  pix_.resize( pixValue.size() , tmp );
  //setD( pixValue.size() );
  setH( pixValue[0].size() );
  setW( pixValue[0][0].size() );
  for(int h=0 ; h<D() ; h++ )
    for(int i=0 ; i<H() ; i++ )
      for(int j=0 ; j<W() ; j++ )
	setPix(h,i,j,pixValue[h][i][j]);
  scale_.resize(3);
  setZScale(zScale);
  setXScale(xScale);
  setYScale(yScale);
  setId("StackFromPixels");
}

Stack::Stack( int dValue,int hValue,int wValue,
	      double zScale,double xScale,double yScale ) {
  setD( hValue );
  setH( hValue );
  setW( wValue );
  scale_.resize(3);
  setZScale(zScale);
  setXScale(xScale);
  setYScale(yScale);
}

Stack::Stack( char *directory,double zScale,double xScale,
	      double yScale,int flag ) {
  readStack(directory,flag);
  scale_.resize(3);
  setZScale(zScale);
  setXScale(xScale);
  setYScale(yScale);
}

Stack::Stack( std::string directory,double zScale,double xScale,
	      double yScale,int flag ) {
  readStack(directory,flag);
  scale_.resize(3);
  setZScale(zScale);
  setXScale(xScale);
  setYScale(yScale);
}

Stack::~Stack() {
  
}

Stack & Stack::operator=( const Stack & stackCopy ) {

  std::cerr << "Stack & Stack::operator=( const Stack & stackCopy )"
       << " not yet defined\n";
//    setH( stackCopy.H() );
//    setW( stackCopy.W() );
//    for(int i=0 ; i<H() ; i++ )
//      for(int j=0 ; j<W() ; j++ )
//        setPix(i,j,stackCopy.pix(i,j));
//    setId( stackCopy.id() );

//    return (*this);
}

int Stack::readStack( char *directory, int flag ) {

  if( flag==0 ) 
    return readStackTiff(directory);
  else {
    std::cerr << "Stack::readStack(char *dir,int flag): "
	 << "Reading from tiff files (flag=0) implemented sofar!\n";
    exit(0);
  }
}

int Stack::readStack( std::string directory, int flag ) {

  if( flag==0 ) 
    return readStackTiff(directory);
  else {
    std::cerr << "Stack::readStack(std::string dir, int flag): "
	 << "Reading from tiff files (flag=0) implemented sofar!\n";
    exit(0);
  }
}

//!Used when reorganizing the z-lattice into more lattice points
/*!Interpolates between old points using a mean of interpolated
  polynomials of order polDeg. The mean is built up from
  interpolations from different sets of polDeg+1 old points around the
  new point, where the new point is within the region.*/
void Stack::interpolatePolynomialZ(double newZScale,int polDeg) {
  
  if( newZScale > zScale() ) {
    std::cerr << "Stack::interpolatePolynomialZ(...)\n"
	 << "Can only be used if number of new lattice points are more"
	 << " than the old number...\nNo reorganization of latttice!\n";
    exit(-1);
  }
  if( polDeg<=0 || polDeg>=D() ) {
    std::cerr << "Stack::interpolatePolynomialZ(...)\n"
	 << "Wrong degree of polynomial. Must be in {1," << D()-1
	 << "}, but is " << polDeg << "!\n";
    exit(-1);
  }
  
  int DD=D()-1;
  double zSpan = zScale()*DD;
  int newNumPoints = int( zSpan/newZScale +0.5 );
  double newZSpan = newNumPoints*newZScale;
  
  //Introduce the new z-positions
  std::vector<double> zPos(newNumPoints);
  for( int i=0 ; i<zPos.size() ; i++ )
    zPos[i] = i*newZScale;

  std::vector< std::vector< std::vector<double> > > tmpPix( newNumPoints );
  for( int h=0 ; h<tmpPix.size() ; h++ ) {
    tmpPix[h].resize( H() );
    for( int i=0 ; i<tmpPix[h].size() ; i++ )
      tmpPix[h][i].resize( W() );
  }
  
  //Group new points into old intervals
  //Use the fact that both are "sorted" from lowest to highest value
  int newCount=0;
  std::vector< std::vector<int> > intervalGroup( DD );
  std::vector<int> intervalCounter( DD );
  for( int h=1 ; h<DD ; h++ ) {
    double z = h*zScale();
    while( zPos[newCount]<z )
      intervalGroup[h-1].push_back( newCount++ );
  }
  //Add the rest of the points to the last interval
  while( newCount<newNumPoints )
    intervalGroup[DD-1].push_back( newCount++ );
  
  //print the result to check on consistancy
//    std::cerr << D() << " " << newNumPoints << "\n";
//    std::cerr << DD*zScale() << " " << (newNumPoints-1)*newZScale << "\n";
//    for( int i=0 ; i<DD ; i++ ) {
//      if( i==0 )
//        std::cerr << "inf";
//      else
//        std::cerr << i*zScale();
//      if( i<DD-1 )
//        std::cerr << "-" << (i+1)*zScale() << " ";
//      else
//        std::cerr << "-inf   ";
//      for( int j=0 ; j<intervalGroup[i].size() ; j++ )
//        std::cerr << intervalGroup[i][j] << " " << zPos[intervalGroup[i][j]]
//  	   << "  ";
//      std::cerr << "\n";
//    }
//  exit(0);
    
  //Do all the interpolations
  int numPolPoints=polDeg+1;
  std::vector<double> x(numPolPoints),y(numPolPoints);
  for( int i=0 ; i<H() ; i++ ) {
    for( int j=0 ; j<W() ; j++ ) {

      for( int n=0 ; n<=DD-numPolPoints+1 ; n++ ) {
	int minPoint= n;
	int maxPoint= n+numPolPoints;
	for( int h=minPoint ; h<maxPoint ; h++ ) {
	  x[h-minPoint] = h*zScale();
	  y[h-minPoint] = pix(h,i,j);
	}
	for( int h=minPoint ; h<maxPoint-1 ; h++ ) {
	  if( i==0 && j==0 )
	    intervalCounter[h]++;
	  for( int hh=0 ; hh<intervalGroup[h].size() ; hh++ ) {
	    tmpPix[ intervalGroup[h][hh] ][i][j] += 
	      polint(x,y,zPos[ intervalGroup[h][hh] ]);
	  }    
	}
      }
    }
  }
  
  //Take mean of added polynomials (different intervals counted different
  //number of times
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      for( int h=0 ; h<DD ; h++ )
	for( int hh=0 ; hh<intervalGroup[h].size() ; hh++ )
	  tmpPix[ intervalGroup[h][hh] ][i][j] /= double( intervalCounter[h] );

  //Normalize the result to span all intensity values...
  double min=tmpPix[0][0][0],max=tmpPix[0][0][0];
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      for( int h=0 ; h<tmpPix.size() ; h++ ) {	
	if( tmpPix[h][i][j]<min )
	  min = tmpPix[h][i][j];
	if( tmpPix[h][i][j]>max )
	  max = tmpPix[h][i][j];
      }
  double delta=max-min;
  if( delta != 0. )
    for( int i=0 ; i<H() ; i++ )
      for( int j=0 ; j<W() ; j++ )
	for( int h=0 ; h<tmpPix.size() ; h++ ) {
	  tmpPix[h][i][j] -= min;
	  tmpPix[h][i][j] *= 255./delta;
	}
  
  std::cerr << "Resizing the stack\n";
//    std::cerr << newNumPoints << "x" << H() << "x" << W() << "=" 
//         << newNumPoints*H()*W() << " doubles needed approximatley "
//         << newNumPoints*H()*W()*8/(1024*1024) << "MB";
  //Copy the result into the stack
  Image tmpImage(tmpPix[0]);
  pix_.resize(newNumPoints,pix_[0]);
  for( int h=0 ; h<D() ; h++ )
    for( int i=0 ; i<H() ; i++ )
      for( int j=0 ; j<W() ; j++ )
  	setPix(h,i,j,tmpPix[h][i][j]);
}

//!Returns the inter/exterpolated value from a polynomial created from (x,y)
/*!This function uses the fact that any K points can be used to create
  a specific polynomial of order K-1. This is used to interpolate
  between points.*/
double Stack::polint(std::vector<double> &xa, std::vector<double> &ya, double x) {
  
  int N = xa.size();

  double y=0.;
  for( int i=0 ; i<N ; i++ ) {
    double nominator=1.,denominator=1.;
    for( int ii=0 ; ii<N ; ii++ )
      if( i != ii ) {
	nominator *= (x-xa[ii]);
	denominator *= (xa[i]-xa[ii]);
      }
    y += ya[i]*nominator/denominator;
  }
  return y;
}

//!Reads a stack of tiff-images, order sets the z-axis
int Stack::readStackTiff( char *directory,int zOrder ) {

  //define and open directory
  std::vector<std::string> filename;
  DIR *dir;
  struct dirent *entry;
  dir = opendir(directory);

  if (!dir) {
    std::cerr << "Stack::readStackTiff( char *directory )\n" 
	      << "Could not open directory " << directory << "\n";
    exit(-1);
  }
  else {
    while (entry = readdir(dir)) {
      std::ostringstream ost;
      ost << directory << "/" << entry->d_name;
      std::string tmp = ost.str();
      
      std::string::size_type approx = tmp.find(".tif");
      std::string::size_type size = tmp.size();
      if( approx>0 && approx<size )
	filename.push_back( tmp );
    }
    closedir(dir);
  }	
  //Sort the filenames (by numbers indicating stack order...)
  sort( filename.begin(),filename.end() );
  
  if( filename.size() ) {
    //Load first image and use that when the stack is expanded
    Image Itmp( filename[0] );
    pix_.resize( filename.size() , Itmp );
    std::cerr << "Loading " << D() << " images of size " << Itmp.H() << "x" 
	      << Itmp.W() << " into stack\n"; 
    
    //Reading from below to get z-axis towards top (assuming stack from top)
    if( zOrder==-1 ) {
      int z=0;
      for( int h=D()-1 ; h>=0 ; h-- ) {
	std::cerr << filename[h] << "\n";
	pix_[z].readImageTiff( filename[h] );
	z++;
      }
    }
    else {
      for( int h=0 ; h<D() ; h++ ) {
	std::cerr << filename[h] << "\n";
	pix_[h].readImageTiff( filename[h] );
      }
    }    
  }
  else
    std::cerr << "int Stack::readStackTiff( char *directory ):\n"
	      << "No tiff images (*.tif) recognized in directory " 
	      << directory << "\n"; 
}

int Stack::readStackTiff( std::string directory ) {
  char *tmp = const_cast<char*>( directory.c_str() );
  return readStackTiff( tmp );
}

int Stack::writeStack( char *directory, char *name, int flag ) {
  
  if( flag==0 ) 
    return writeStackTiff(directory,name);
  else {
    std::cerr << "Stack::writeStack(): "
	      << "Only writing to tiff files (flag=0) implemented sofar!\n";
    exit(0);
  }
}

int Stack::writeStack( std::string directory,std::string name, int flag ) {
  
  if( flag==0 ) 
    return writeStackTiff(directory,name);
  else {
    std::cerr << "Stack::writeStack(): "
	      << "Only writing to tiff files (flag=0) implemented sofar!\n";
    exit(0);
  }
} 

//! Writes the stack in tiff files named directory/name###.tif 
int Stack::writeStackTiff( char *directory, char *name ) {
  
  //Add '/' if dir name doesn't end with it (for creating correct filenames)
  std::string tmp = directory;
  if( tmp[tmp.size()-1] != '/' )
    tmp.push_back('/');
  directory = const_cast<char*>( tmp.c_str() );
  
  //Check if the directory is there
  DIR *dir;
  dir = opendir(directory);
  if (!dir) {
    std::cerr << "int Stack::writeStackTiff( char *dir, char *name="" )\n" 
	      << "Could not open directory " << directory << ", "
	      << "no stack written!\n";
    return -1;
  }
  closedir(dir);
  
  //Write files in the directory
  //Caveat: no check for overwriting of old files...
  std::vector<int> ticker(3,0);
  
  std::cerr << "Writing " << D() << " images of size " << H() << "x" << W() 
	    << " in directory " << directory << "\n";
  //for( int h=D()-1 ; h>=0 ; h-- ) {//for z axis towards apex
  for( int h=0 ; h<D() ; h++ ) {
    
    std::ostringstream ost;
    ost << directory << name << ticker[0] << ticker[1] << ticker[2] << ".tif";
    std::string filename = ost.str();
    
    //std::cerr << "Writing " << filename << "\n";
    pix_[h].writeImageTiff( const_cast<char*>( filename.c_str() ) );
    
    //Increase number in filename...
    ticker[2]++;
    if( ticker[2]>9 ) { //adjust ticker xXx
      ticker[1]++;
      ticker[2]=0;
    }
    if( ticker[1]>9 ) { //adjust ticker Xxx
      ticker[0]++;
      ticker[2]=ticker[1]=0;
    }
    if( ticker[0]>9 ) {
      std::cerr << "int Stack::writeStackTiff( char *directory, " 
		<< "char *name="" ):\n"
		<< "Warning ticker type in filename lost since counter "
		<< "exceeds 999\n" 
		<< "(May cause sorting problems of filenames)\n\n";
    }
  } 
  return 0;
}

//! Writes the stack in xDim in tiff files named directory/name###.tif 
int Stack::writeXStackTiff( char *directory, char *name, int x0, 
			    int y0, int r ) {
  
  //Add '/' if dir name doesn't end with it (for creating correct filenames)
  std::string tmp = directory;
  if( tmp[tmp.size()-1] != '/' )
    tmp.push_back('/');
  directory = const_cast<char*>( tmp.c_str() );
  
  //Check if the directory exists
  DIR *dir;
  dir = opendir(directory);
  if (!dir) {
    std::cerr << "int Stack::writeXStackTiff( "
	      << "char *directory, char *name="" )\n" 
	      << "Could not open directory " << directory << ", "
	      << "no stack written!\n";
    return -1;
  }
  closedir(dir);
  
  //Create a new stack with rotated dimensions
  // X -> Z, Z -> Y, Y -> X

  //Define the new dimensions...
  if( x0==-1 ) x0=H()/2; 
  if( y0==-1 ) y0=W()/2; 
  if( r==-1 ) r=H()/2; 
  int newZSize = 2*r;//new number of pictures
  int newXSize = D();//new height of the pictures
  int newYSize = 2*r;//new width of the pictures
  
  std::vector< std::vector< std::vector<double> > > newI(newZSize);
  for(int h=0 ; h<newZSize ; h++ ) {
    newI[h].resize( newXSize );
    for(int i=0 ; i<newXSize ; i++ )
      newI[h][i].resize( newYSize );
  }

  //Copy pixels from the stack
  int x=x0-r;
  for(int h=0 ; h<newZSize ; h++ ) {
    int z=0;
    for(int i=0 ; i<newXSize ; i++ ) {
      int y=y0-r;
      for(int j=0 ; j<newYSize ; j++ ) {
	if( z>=0 && x>=0 && y>=0 && z<D() && x<H() && y<W() )
	  newI[h][i][j] = pix(z,x,y);
	else
	  newI[h][i][j] = 0;
	y++;
      }
      z++;
    }
    x++;
  }
  
  Stack Stmp( newI );
  Stmp.writeStackTiff( directory, name ); 
}

//! Writes the stack in yDim in tiff files named directory/name###.tif 
int Stack::writeYStackTiff( char *directory, char *name, int x0, 
			    int y0, int r ) {
  
  //Add '/' if dir name doesn't end with it (for creating correct filenames)
  std::string tmp = directory;
  if( tmp[tmp.size()-1] != '/' )
    tmp.push_back('/');
  directory = const_cast<char*>( tmp.c_str() );
  
  //Check if the directory exists
  DIR *dir;
  dir = opendir(directory);
  if (!dir) {
    std::cerr << "int Stack::writeXStackTiff( "
	      << "char *directory, char *name="" )\n" 
	      << "Could not open directory " << directory << ", "
	      << "no stack written!\n";
    return -1;
  }
  closedir(dir);
  
  //Create a new stack with rotated dimensions
  // Y -> Z, Z -> Y, X -> X

  //Define the new dimensions...
  if( x0==-1 ) x0=H()/2; 
  if( y0==-1 ) y0=W()/2; 
  if( r==-1 ) r=W()/2; 
  int newZSize = 2*r;//new number of pictures
  int newXSize = D();//new height of the pictures
  int newYSize = 2*r;//new width of the pictures
  
  std::vector< std::vector< std::vector<double> > > newI(newZSize);
  for(int h=0 ; h<newZSize ; h++ ) {
    newI[h].resize( newXSize );
    for(int i=0 ; i<newXSize ; i++ )
      newI[h][i].resize( newYSize );
  }

  //Copy pixels from the stack
  int y=y0-r;
  for(int h=0 ; h<newZSize ; h++ ) {
    int z=0;
    for(int i=0 ; i<newXSize ; i++ ) {
      int x=x0-r;
      for(int j=0 ; j<newYSize ; j++ ) {
	if( z>=0 && x>=0 && y>=0 && z<D() && x<H() && y<W() )
	  newI[h][i][j] = pix(z,x,y);
	else
	  newI[h][i][j] = 0;
	x++;
      }
      z++;
    }
    y++;
  }
  
  Stack Stmp( newI );
  Stmp.writeStackTiff( directory, name ); 
}

//! Writes a new stack defined by rotating around the x0,y0 point
int Stack::writeRStackTiff( char *directory, char *name, int x0, 
			    int y0, int r, int deltaPhi ) {
  
  //Add '/' if dir name doesn't end with it (for creating correct filenames)
  std::string tmp = directory;
  if( tmp[tmp.size()-1] != '/' )
    tmp.push_back('/');
  directory = const_cast<char*>( tmp.c_str() );

  //Check if the directory exists
  DIR *dir;
  dir = opendir(directory);
  if (!dir) {
    std::cerr << "int Stack::writeRStackTiff( "
	      << "char *directory, char *name="" )\n" 
	      << "Could not open directory " << directory << ", "
	      << "no stack written!\n";
    return -1;
  }
  closedir(dir);
  
  
  //Create a new stack by rotating around x0,y0,... 
  //Define the new dimensions...
  if( x0==-1 ) x0=H()/2; 
  if( y0==-1 ) y0=W()/2; 
  if( r==-1 ) r=W()/2; 
  if( deltaPhi<=0 || deltaPhi>360 ) {
    std::cerr << "int Stack::writeRStackTiff(...)" 
	 << "deltaPhi must be in {0,360]\n";
    exit(-1);
  }
  int newZSize = 360/deltaPhi;//new number of pictures
  int newXSize = D();//new height of the pictures
  int newYSize = 2*r;//new width of the pictures
  
  std::vector< std::vector< std::vector<double> > > newI(newZSize);
  for(int h=0 ; h<newZSize ; h++ ) {
    newI[h].resize( newXSize );
    for(int i=0 ; i<newXSize ; i++ )
      newI[h][i].resize( newYSize );
  }

  //Copy pixels from the stack
  double phi=0;
  double PI=3.1415;
  for(int h=0 ; h<newZSize ; h++ ) {
    //Set the starting point...
    double xD=x0-r*std::sin(2.*PI*phi/360.);
    double yD=y0-r*std::cos(2.*PI*phi/360.);
    double deltaX = x0-xD;
    double deltaY = y0-yD;
    double norm = std::sqrt( deltaX*deltaX + deltaY*deltaY );
    deltaX /= norm;
    deltaY /= norm;
    for(int j=0 ; j<newYSize ; j++ ) {
      int x = int( xD+0.5 ); 
      int y = int( yD+0.5 ); 
      int z=D()-1;
      for(int i=0 ; i<newXSize ; i++ ) {
	//std::cerr << z << " " << x << " " << y << " | " 
	//<< h << " " << i << " " 
	//   << j << " " << newI[h][i][j] << " b\n";
	if( z>=0 && x>=0 && y>=0 && z<D() && x<H() && y<W() )
	  newI[h][i][j] = pix(z,x,y);
	else
	  newI[h][i][j] = 0.;
	//std::cerr << z << " " << x << " " << y << " | " 
	//<< h << " " << i << " " 
	//   << j << "\t" << newI[h][i][j] << "\n";
	z--;
      }
      xD += deltaX;
      yD += deltaY;
    }
    phi+=deltaPhi;
  }
  Stack Stmp( newI );
  Stmp.writeStackTiff( directory, name ); 
}


int Stack::writeStackTiff( std::string directory, std::string name ) {
  writeStackTiff( const_cast<char*>( directory.c_str() ),
		  const_cast<char*>( name.c_str() ) );
}

//!Writes the stack in black and white and adds red pixels
int Stack::writeStackTiffWithRedPixels( char *directory, 
					std::vector< std::vector<int> > 
					&redPixels, char *name ) {
  

  //Add '/' if dir name doesn't end with it (for creating correct filenames)
  std::string tmp = directory;
  if( tmp[tmp.size()-1] != '/' )
    tmp.push_back('/');
  directory = const_cast<char*>( tmp.c_str() );
  
  //Check if the directory is there
  DIR *dir;
  dir = opendir(directory);
  if (!dir) {
    std::cerr << "int Stack::writeStackTiff( "
	      << "char *directory, char *name="" )\n" 
	      << "Could not open directory " << directory << ", "
	      << "no stack written!\n";
    return -1;
  }
  closedir(dir);
  
  //Write files in the directory
  //Caveat: no check for overwriting of old files...
  std::vector<int> ticker(3,0);
  std::cerr << "Writing " << D() << " images of size " 
	    << H() << "x" << W() << " in directory " << directory << "\n";
  
  //for( int h=D()-1 ; h>=0 ; h-- ) {//For stacks with z axis towards top
  for( int h=0 ; h<D() ; h++ ) {
    std::ostringstream ost;
    ost << directory << name << ticker[0] << ticker[1] << ticker[2] << ".tif";
    std::string filename = ost.str();
    //Find red pixels in this image...
    std::vector<int> intTmp(2);
    std::vector< std::vector<int> > ImageRedPix;
    for( int n=0 ; n<redPixels.size() ; n++ )
      if( redPixels[n][0] == h ) {
	intTmp[0] = redPixels[n][1];
	intTmp[1] = redPixels[n][2];
	ImageRedPix.push_back( intTmp );
      }
    //std::cerr << "Writing " << filename << " with red pixels\n";
    pix_[h].writeImageTiffWithRedPixels(const_cast<char*>( filename.c_str() ),
					ImageRedPix);
    
    //Increase number in filename...
    ticker[2]++;
    if( ticker[2]>9 ) { //adjust ticker xXx
      ticker[1]++;
      ticker[2]=0;
    }
    if( ticker[1]>9 ) { //adjust ticker Xxx
      ticker[0]++;
      ticker[2]=ticker[1]=0;
    }
    if( ticker[0]>9 ) {
      std::cerr << "int Stack::writeStackTiff( "
		<< "char *directory, char *name="" ):\n"
		<< "Warning ticker type in filename lost since counter "
		<< "exceeds 999\n(May cause sorting problems of "
		<< "filenames)\n\n";
    }
  } 
  return 0;
}

//!Writes the stack in black and white and adds red and blue pixels
int Stack::writeStackTiffWithRedBluePixels( char *directory, 
					    std::vector< std::vector<int> > 
					    &redPixels,
					    std::vector< std::vector<int> > 
					    &bluePixels, char* name ) {
  

  //Add '/' if dir name doesn't end with it (for creating correct filenames)
  std::string tmp = directory;
  if( tmp[tmp.size()-1] != '/' )
    tmp.push_back('/');
  directory = const_cast<char*>( tmp.c_str() );

  //Check if the directory is there
  DIR *dir;
  dir = opendir(directory);
  if (!dir) {
    std::cerr << "int Stack::writeStackTiff( char *directory, char *name="" )\n" 
	 << "Could not open directory " << directory << ", "
	 << "no stack written!\n";
    return -1;
  }
  closedir(dir);

  //Write files in the directory
  //Caveat: no check for overwriting of old files...
  std::vector<int> ticker(3,0);

  std::cerr << "Writing " << D() << " images of size " << H() 
	    << "x" << W() << "in directory " << directory << "\n";
  
  //for( int h=D()-1 ; h>=0 ; h-- ) {//For z-axis towards apex
  for( int h=0 ; h<D() ; h++ ) {
    
    std::ostringstream ost;
    ost << directory << name << ticker[0] << ticker[1] << ticker[2] << ".tif";
    std::string filename = ost.str();
    //Find red pixels in this image...
    std::vector<int> intTmp(2);
    std::vector< std::vector<int> > ImageRedPix,ImageBluePix;
    for( int n=0 ; n<redPixels.size() ; n++ )
      if( redPixels[n][0] == h ) {
	intTmp[0] = redPixels[n][1];
	intTmp[1] = redPixels[n][2];
	ImageRedPix.push_back( intTmp );
      }
    for( int n=0 ; n<bluePixels.size() ; n++ )
      if( bluePixels[n][0] == h ) {
	intTmp[0] = bluePixels[n][1];
	intTmp[1] = bluePixels[n][2];
	ImageBluePix.push_back( intTmp );
      }
    //std::cerr << "Writing " << filename << " with red and blue pixels\n";
    pix_[h].writeImageTiffWithRedBluePixels(const_cast<char*>
					    ( filename.c_str() ),
					    ImageRedPix,ImageBluePix);
    
    //Increase number in filename...
    ticker[2]++;
    if( ticker[2]>9 ) { //adjust ticker xXx
      ticker[1]++;
      ticker[2]=0;
    }
    if( ticker[1]>9 ) { //adjust ticker Xxx
      ticker[0]++;
      ticker[2]=ticker[1]=0;
    }
    if( ticker[0]>9 ) {
      std::cerr << "int Stack::writeStackTiff( "
		<< "char *directory, char *name="" ):\n"
		<< "Warning ticker type in filename lost since counter "
		<< "exceeds 999\n(May cause sorting problems of "
		<< "filenames)\n\n";
    }
  } 
  return 0;  
}

//!Writes all pixels in random colors grouped according to the boa pixels
int Stack::writeStackTiffWithRandomColors(char *directory,
					  std::vector< std::vector< 
					  std::vector<int> > > &boa,
					  char* name) {
  //Add '/' if dir name doesn't end with it (for creating correct filenames)
  std::string tmp = directory;
  if( tmp[tmp.size()-1] != '/' )
    tmp.push_back('/');
  directory = const_cast<char*>( tmp.c_str() );
  
  //Check if the directory is there
  DIR *dir;
  dir = opendir(directory);
  if (!dir) {
    std::cerr << "int Stack::writeStackTiff( "
	      << "char *directory, char *name="" )\n" 
	      << "Could not open directory " << directory << ", "
	      << "no stack written!\n";
    return -1;
  }
  closedir(dir);
  
  //Define a color vector and add random colors into it
  std::vector< std::vector<char> > color( boa.size() );
  for( int i=0 ; i<color.size() ; i++ ) {
    color[i].resize(3);
    color[i][0] = char( random()*254 );//red
    color[i][1] = char( random()*254 );//green
    color[i][2] = char( random()*254 );//blue
  }
  
  //Write files in the directory
  //Caveat: no check for overwriting of old files...
  std::vector<int> ticker(3,0);
  
  std::cerr << "Writing " << D() << " images of size " 
	    << H() << "x" << W() << " in directory " << directory << "\n";
  
  //for( int h=D()-1 ; h>=0 ; h-- ) {//For z-axis in apex direction
  for( int h=0 ; h<D() ; h++ ) {
    
    std::ostringstream ost;
    ost << directory << name << ticker[0] << ticker[1] << ticker[2] << ".tif";
    std::string filename = ost.str();
    //Find the boas connected to this image
    std::vector<int> intTmp(2);
    std::vector< std::vector< std::vector<int> > > imageBoa;
    std::vector< std::vector<char> > imageColor;
    int imageBoaCounter=0;
    for( int n=0 ; n<boa.size() ; n++ ) {
      int includedFlag=0;
      for( int m=0 ; m<boa[n].size() ; m++ ) {
	if( boa[n][m][0] == h ) {
	  intTmp[0] = boa[n][m][1];
	  intTmp[1] = boa[n][m][2];
	  if( includedFlag==0 ) {
	    imageBoaCounter++;
	    imageBoa.resize(imageBoaCounter);
	  }
	  imageBoa[imageBoaCounter-1].push_back( intTmp );
	  includedFlag++;
	}
      }
      if( includedFlag )
	imageColor.push_back( color[n] );
    }
    if( imageBoa.size() != imageColor.size() ) {
      std::cerr << "Stack::writeStackTiffWithRandomColors - "
		<< "Wrong boa or color sizes\n";
      exit(-1);
    }
    //std::cerr << "Writing " << filename << " with " << imageColor.size()
    //      << "defined colors\n";
    pix_[h].writeImageTiffWithDefinedColors(const_cast<char*>
					    ( filename.c_str() ),
					    imageBoa,imageColor);
    
    //Increase number in filename...
    ticker[2]++;
    if( ticker[2]>9 ) { //adjust ticker xXx
      ticker[1]++;
      ticker[2]=0;
    }
    if( ticker[1]>9 ) { //adjust ticker Xxx
      ticker[0]++;
      ticker[2]=ticker[1]=0;
    }
    if( ticker[0]>9 ) {
      std::cerr << "int Stack::writeStackTiffRandomColor - "
		<< "Warning ticker type in filename lost since counter exceeds"
		<< " 999\n(May cause sorting problems of filenames)\n\n";
    }
  } 
  return 0;
}

//!Function for finding maximas by a local search algorithm
/*! This function starts from each pixel in the picture and tries to
  find the closest maxima using a simple gradient descent walk. It
  then returns the pixels which are maxima to more than threshold number of
  pixels. If valThresoldFlag is set only maximas greater than
  valThreshold is returned*/
int Stack::findMaximaByLocalSearch(std::vector< std::vector<int> > &hijMax,
				   std::vector< std::vector< 
				   std::vector<int> > > &flag,
				   double valThreshold, 
				   int valThresholdFlag,
				   int threshold ) {

  std::vector<int> tmp(4);
  if( hijMax.size() )
    hijMax.resize(0);
  std::vector< std::vector<int> > hijTmp;//maxima positions before thresholds 
  std::vector< std::vector<int> > walkTmp;//positions for one walk (start point)

  // Marker for which pixels that have been visited
  flag.resize( D() );
  for( int i=0 ; i<flag.size() ; i++ ) {
    flag[i].resize( H() );
    for( int j=0 ; j<flag[i].size() ; j++ )
      flag[i][j].resize( W(),0 );
  }
  //Temporary marker for pixels 
  std::vector< std::vector< std::vector<int> > > tmpFlag( D() );
  for( int i=0 ; i<tmpFlag.size() ; i++ ) {
    tmpFlag[i].resize( H() );
    for( int j=0 ; j<tmpFlag[i].size() ; j++ )
      tmpFlag[i][j].resize( W(),0);
  }


  //Find the maxima, starting from each pixel
  for( int hStart=0 ; hStart<D() ; hStart++ )
    for( int iStart=0 ; iStart<H() ; iStart++ )
      for( int jStart=0 ; jStart<W() ; jStart++ ) {
	int h=hStart;
	int i=iStart;
	int j=jStart;
	double value,newValue;
	walkTmp.resize(1);
	walkTmp[0].resize(3);
	walkTmp[0][0]=h;walkTmp[0][1]=i;walkTmp[0][2]=j;
	
	if( i==0 && j==0 )
	  std::cerr << "Z=" << h << "\n";
	//find the max by walking uphill (greedy)
	if( !flag[h][i][j] ) {
	  do {
	    tmpFlag[h][i][j]=1;
	    newValue=value=pix(h,i,j);
	    int newH=h,newI=i,newJ=j;
	    //Check all pixels around a given pixel
//  	    for(int hh=h-1 ; hh<=h+1 ; hh++ )
//  	      for(int ii=i-1 ; ii<=i+1 ; ii++ )
//  		for(int jj=j-1 ; jj<=j+1 ; jj++ )
//  		  if( hh>=0 && ii>=0 && jj>=0 && hh<D() && ii<H() && jj<W() )
//  		    if( pix(hh,ii,jj)>newValue ) {
//  		      newValue=pix(hh,ii,jj);
//  		      newH=hh;
//  		      newI=ii;
//  		      newJ=jj;
//  		    }
	    //Check only nearest neighbors
	    for(int a=-1 ; a<=1 ; a+=2 ) {
	      int hh = h+a;
	      if( hh>=0 && hh<D() && pix(hh,i,j)>=newValue ) {
		newValue=pix(hh,i,j);
		newH=hh;
		newI=i;
		newJ=j;
	      }
	      int ii = i+a;
	      if( ii>=0 && ii<H() && pix(h,ii,j)>newValue ) {
		newValue=pix(h,ii,j);
		newH=h;
		newI=ii;
		newJ=j;
	      }
	      int jj = j+a;  
	      if( jj>=0 && jj<W() && pix(h,i,jj)>newValue ) {
		newValue=pix(h,i,jj);
		newH=h;
		newI=i;
		newJ=jj;
	      }
	    }
	    h=newH;
	    i=newI;
	    j=newJ;	
	    std::vector<int> tmpPos(3);
	    tmpPos[0]=h;tmpPos[1]=i;tmpPos[2]=j;
	    walkTmp.push_back( tmpPos );
	  } while( newValue>value && !flag[h][i][j] && !tmpFlag[h][i][j] );
	  tmpFlag[h][i][j]=1;
	}
	//Recursivly add plateau
	if( !flag[h][i][j] ) {
	  //std::cerr << "SaddPlat " << pix(h,i,j) << " " << h << " " << i 
	  //    << " " << j << "\n";
	  addPlateauNeighborRecursive(pix(h,i,j),h,i,j,tmpFlag,walkTmp);
	}
	//Collect the path data and add one visit for the maximum
	if( !flag[h][i][j] ) { //new maximum
	  tmp[0]=h;tmp[1]=i;tmp[2]=j;tmp[3]=1;
	  hijTmp.push_back( tmp );
	  int n=hijTmp.size();
	  for( int a=0 ; a<walkTmp.size() ; a++ )
	    flag[ walkTmp[a][0] ][ walkTmp[a][1] ][ walkTmp[a][2] ] = n;
	}
	else { //old maximum
	  int n = flag[h][i][j];
	  for( int a=0 ; a<walkTmp.size() ; a++ )
	    flag[ walkTmp[a][0] ][ walkTmp[a][1] ][ walkTmp[a][2] ] = n;
	  hijTmp[n-1][3]++;
	}
      }
  
  //Get the maxima visited more than threshold times and with an intensity
  // higher than valThreshold...
  //
  for( int n=0 ; n<hijTmp.size() ; n++ )
    if( hijTmp[n][3]>=threshold && 
	( !valThresholdFlag || 
	  pix(hijTmp[n][0],hijTmp[n][1],hijTmp[n][2])>=valThreshold ) )
      hijMax.push_back( hijTmp[n] ); 
  
  return hijMax.size();
}

//!Function for finding maximas by a local search algorithm
/*! This function starts from each pixel in the picture and tries to
  find the closest maxima using a simple gradient descent walk. It
  then returns the pixels which are maxima to more than threshold number of
  pixels. If valThresoldFlag is set only maximas greater than
  valThreshold is returned. In bg the background pixels are stored and copied as -1 into the flag.*/
int Stack::findMaximaByLocalSearch(std::vector< std::vector<int> > &hijMax,
				   std::vector< std::vector< 
				   std::vector<int> > > &flag,
				   std::vector< std::vector<int> > &bg,
				   double valThreshold, 
				   int valThresholdFlag,
				   int threshold ) {

  std::vector<int> tmp(4);
  if( hijMax.size() )
    hijMax.resize(0);
  std::vector< std::vector<int> > hijTmp;//maxima positions before thresholds 
  std::vector< std::vector<int> > walkTmp;//positions for one walk (start point)

  // Marker for which pixels that have been visited
  flag.resize( D() );
  for( int i=0 ; i<flag.size() ; i++ ) {
    flag[i].resize( H() );
    for( int j=0 ; j<flag[i].size() ; j++ )
      flag[i][j].resize( W(),0 );
  }
  //Temporary marker for pixels 
  std::vector< std::vector< std::vector<int> > > tmpFlag( D() );
  for( int i=0 ; i<tmpFlag.size() ; i++ ) {
    tmpFlag[i].resize( H() );
    for( int j=0 ; j<tmpFlag[i].size() ; j++ )
      tmpFlag[i][j].resize( W(),0);
  }
  for( int i=0 ; i<bg.size() ; i++ ) {
    flag[ bg[i][0] ][ bg[i][1] ][ bg[i][2] ]=-1;
    tmpFlag[ bg[i][0] ][ bg[i][1] ][ bg[i][2] ]=-1;
  }

  //Find the maxima, starting from each pixel
  for( int hStart=0 ; hStart<D() ; hStart++ )
    for( int iStart=0 ; iStart<H() ; iStart++ )
      for( int jStart=0 ; jStart<W() ; jStart++ ) {
	int h=hStart;
	int i=iStart;
	int j=jStart;
	double value,newValue;
	walkTmp.resize(1);
	walkTmp[0].resize(3);
	walkTmp[0][0]=h;walkTmp[0][1]=i;walkTmp[0][2]=j;
	
	if( i==0 && j==0 )
	  std::cerr << "Z=" << h << "\n";
	//find the max by walking uphill (greedy)
	if( !flag[h][i][j] ) {
	  do {
	    tmpFlag[h][i][j]=1;
	    newValue=value=pix(h,i,j);
	    int newH=h,newI=i,newJ=j;
	    //Check all pixels around a given pixel
//  	    for(int hh=h-1 ; hh<=h+1 ; hh++ )
//  	      for(int ii=i-1 ; ii<=i+1 ; ii++ )
//  		for(int jj=j-1 ; jj<=j+1 ; jj++ )
//  		  if( hh>=0 && ii>=0 && jj>=0 && hh<D() && ii<H() && jj<W() )
//  		    if( pix(hh,ii,jj)>newValue ) {
//  		      newValue=pix(hh,ii,jj);
//  		      newH=hh;
//  		      newI=ii;
//  		      newJ=jj;
//  		    }
	    //Check only nearest neighbors
	    for(int a=-1 ; a<=1 ; a+=2 ) {
	      int hh = h+a;
	      if( hh>=0 && hh<D() && pix(hh,i,j)>=newValue ) {
		newValue=pix(hh,i,j);
		newH=hh;
		newI=i;
		newJ=j;
	      }
	      int ii = i+a;
	      if( ii>=0 && ii<H() && pix(h,ii,j)>newValue ) {
		newValue=pix(h,ii,j);
		newH=h;
		newI=ii;
		newJ=j;
	      }
	      int jj = j+a;  
	      if( jj>=0 && jj<W() && pix(h,i,jj)>newValue ) {
		newValue=pix(h,i,jj);
		newH=h;
		newI=i;
		newJ=jj;
	      }
	    }
	    h=newH;
	    i=newI;
	    j=newJ;	
	    std::vector<int> tmpPos(3);
	    tmpPos[0]=h;tmpPos[1]=i;tmpPos[2]=j;
	    walkTmp.push_back( tmpPos );
	  } while( newValue>value && !flag[h][i][j] && !tmpFlag[h][i][j] );
	  tmpFlag[h][i][j]=1;
	}
	//Recursivly add plateau
	//if( !flag[h][i][j] ) {
	  //std::cerr << "SaddPlat " << pix(h,i,j) << " " << h << " " << i 
	  //    << " " << j << "\n";
	  //addPlateauNeighborRecursive(pix(h,i,j),h,i,j,tmpFlag,walkTmp);
	//}
	//Collect the path data and add one visit for the maximum
	if( !flag[h][i][j] ) { //new maximum
	  tmp[0]=h;tmp[1]=i;tmp[2]=j;tmp[3]=1;
	  hijTmp.push_back( tmp );
	  int n=hijTmp.size();
	  for( int a=0 ; a<walkTmp.size() ; a++ )
	    flag[ walkTmp[a][0] ][ walkTmp[a][1] ][ walkTmp[a][2] ] = n;
	}
	else { //old maximum
	  int n = flag[h][i][j];
	  for( int a=0 ; a<walkTmp.size() ; a++ )
	    flag[ walkTmp[a][0] ][ walkTmp[a][1] ][ walkTmp[a][2] ] = n;
	  if( n>0 )
	    hijTmp[n-1][3]++;
	}
      }
  
  //Get the maxima visited more than threshold times and with an intensity
  // higher than valThreshold...
  //
  for( int n=0 ; n<hijTmp.size() ; n++ )
    if( hijTmp[n][3]>=threshold && 
	( !valThresholdFlag || 
	  pix(hijTmp[n][0],hijTmp[n][1],hijTmp[n][2])>=valThreshold ) ) {
      hijMax.push_back( hijTmp[n] ); 
    }
    else {
      std::cerr << "Removing cell " << n << " due to low intensity or too "
		<< "small size.\n";
      for( int h=0 ; h<D() ; h++ )
	for( int i=0 ; i<H() ; i++ )
	  for( int j=0 ; j<W() ; j++ ) {
	    if( flag[h][i][j]==n+1 ) flag[h][i][j]=0;
	    //else if( flag[h][i][j]>n+1 ) flag[h][i][j]--;
	  }
    }
  return hijMax.size();
}

void Stack::addPlateauNeighborRecursive(double value,int h,int i,int j,
					std::vector< std::vector< 
					std::vector<int> > > &tmpFlag,
					std::vector< std::vector<int> > 
					&walkTmp) {

  //Check only nearest neighbors
  for(int a=-1 ; a<=1 ; a+=2 ) {
    int hh = h+a;
    if( hh>=0 && hh<D() && pix(hh,i,j)==value && !tmpFlag[hh][i][j] ) {
      tmpFlag[hh][i][j]=1;
      std::vector<int> tmpPos(3);
      tmpPos[0]=hh;tmpPos[1]=i;tmpPos[2]=j;
      walkTmp.push_back( tmpPos );      
      addPlateauNeighborRecursive(value,hh,i,j,tmpFlag,walkTmp);
    }
    int ii = i+a;
    if( ii>=0 && ii<H() && pix(h,ii,j)==value && !tmpFlag[h][ii][j] ) {
      tmpFlag[h][ii][j]=1;
      std::vector<int> tmpPos(3);
      tmpPos[0]=h;tmpPos[1]=ii;tmpPos[2]=j;
      walkTmp.push_back( tmpPos );      
      addPlateauNeighborRecursive(value,h,ii,j,tmpFlag,walkTmp);
    }
    int jj = j+a;  
    if( jj>=0 && jj<W() && pix(h,i,jj)==value && !tmpFlag[h][i][jj] ) {
      tmpFlag[h][i][jj]=1;
      std::vector<int> tmpPos(3);
      tmpPos[0]=h;tmpPos[1]=i;tmpPos[2]=jj;
      walkTmp.push_back( tmpPos );      
      addPlateauNeighborRecursive(value,h,i,jj,tmpFlag,walkTmp);
    }
  }
}
  

//!Function for finding maximas by a local search algorithm, image by image
/*! This function starts from each pixel in the picture and tries to
  find the closest maxima using a simple gradient descent walk. It
  then returns the pixels which are maxima to more than threshold number of
  pixels. If valThresoldFlag is set only maximas greater than
  valThreshold is returned*/
int Stack::imageFindMaximaByLocalSearch(std::vector< std::vector<int> > 
					&hijMax,
					double valThreshold, 
					int valThresholdFlag,
					int threshold ) {

  std::vector<int> tmp(4);
  if( hijMax.size() )
    hijMax.resize(0);
  std::vector< std::vector<int> > hijTmp,ijMax;//maxima positions before thresholds 

  //Find the maxima, image by image
  for( int h=0 ; h<D() ; h++ ) {
    std::cerr << "Z=" << h << "\n";
    pix_[h].findMaximaByGradientDescent( ijMax );
    tmp[0]=h;
    for( int i=0 ; i<ijMax.size() ; i++ ) {
      for( int j=0 ; j<ijMax[i].size() ; j++ )
	tmp[j+1]=ijMax[i][j];
      hijTmp.push_back( tmp );
    }
  }
  
  //Get the maxima visited more than threshold times and with an intensity
  // higher than valThreshold...
  //
  for( int n=0 ; n<hijTmp.size() ; n++ )
    if( hijTmp[n][3]>=threshold && 
	( !valThresholdFlag || 
	  pix(hijTmp[n][0],hijTmp[n][1],hijTmp[n][2])>=valThreshold ) )
      hijMax.push_back( hijTmp[n] ); 
  
  return hijMax.size();
}

//!Uses a weighted mean of neighbouring pixels to smothen the picture
/*!The radius defines which pixels that will be weighted into the
  smothen mean. A "boxed" constant weight is used for all pixels
  except the central one which is having weight weight. The radius is
  given in "real" units (micrometer,...) which must be "converted"
  into lattice point distances... */
void Stack::smothen(double weight, double radius) {
  
  if( weight<0. ) {
    std::cerr << "Stack::smothen: Wrong weight given...\n";
    exit(-1);
  }

  register int zSize=D();
  register int xSize=H();
  register int ySize=W();

  //To be sure that all possible lattice points will be checked
  int deltaIntZ= int( radius/zScale() + 1. );
  int deltaIntX= int( radius/xScale() + 1. );
  int deltaIntY= int( radius/yScale() + 1. );

  //Introduce latticeData...
  std::vector< std::vector< std::vector<double> > > latticeData( zSize );
  for(int h=0 ; h<zSize ; h++ ) {
    latticeData[h].resize( xSize );
    for(int i=0 ; i<xSize ; i++ )
      latticeData[h][i].resize( ySize );
  }
  
  //To save some multiplications
  double zFac = zScale()*zScale();
  double xFac = xScale()*xScale();
  double yFac = yScale()*yScale();

  for( int h=0 ; h<zSize ; h++ ) {
    for( int i=0 ; i<xSize ; i++ )
      for( int j=0 ; j<ySize ; j++ ) {
	double norm=weight-1.;//1 will be added below
	double value=norm*pix(h,i,j);//1*pix(h,i,j) added below
	for( int hh=h-deltaIntZ ; hh<=h+deltaIntZ ; hh++ )
	  for( int ii=i-deltaIntX ; ii<=i+deltaIntX ; ii++ )
	    for( int jj=j-deltaIntY ; jj<=j+deltaIntY ; jj++ )
	      if( ii>=0 && jj>=0 && hh>=0 && 
		  ii<xSize && jj<ySize && hh<zSize ) { 
		double d=std::sqrt( (hh-h)*(hh-h)*zFac
			       +(ii-i)*(ii-i)*xFac
			       +(jj-j)*(jj-j)*yFac );
		if( d<=radius ) { 
		  value += pix(hh,ii,jj);
		  norm++;
		}
	      }
	if( norm<=0 || value<0. ) {
	  std::cerr << "Stack::smothen: Wrong weight given...\n";
	  exit(-1);
	}
	latticeData[h][i][j] = value/norm;
      }
    std::cerr << " z=" << h << "\n";
  }
  //Copy the values
  for( int h=0 ; h<zSize ; h++ )
    for( int i=0 ; i<xSize ; i++ )
      for( int j=0 ; j<ySize ; j++ )
	setPix(h,i,j,latticeData[h][i][j]);
}

//!A triangular weighted mean of neighbouring pixels for smothening the picture
/*!The radius defines which pixels that will be weighted into the
  smothen mean. A "triangular" (from one in the center to zero at r)
  weight is used for all pixels. The radius is given in "real" units
  (micrometer,...)  which must be "converted" into lattice point
  distances... */
void Stack::triSmothen(double radius) {
  
  register int zSize=D();
  register int xSize=H();
  register int ySize=W();

  //To be sure that all possible lattice points will be checked
  int deltaIntZ= int( radius/zScale() + 1. );
  int deltaIntX= int( radius/xScale() + 1. );
  int deltaIntY= int( radius/yScale() + 1. );

  //Introduce latticeData...
  std::vector< std::vector< std::vector<double> > > latticeData( zSize );
  for(int h=0 ; h<zSize ; h++ ) {
    latticeData[h].resize( xSize );
    for(int i=0 ; i<xSize ; i++ )
      latticeData[h][i].resize( ySize );
  }
  
  //To save some multiplications
  double zFac = zScale()*zScale();
  double xFac = xScale()*xScale();
  double yFac = yScale()*yScale();

  for( int h=0 ; h<zSize ; h++ )
    for( int i=0 ; i<xSize ; i++ )
      for( int j=0 ; j<ySize ; j++ ) {
	double norm=0.;
	double value=0.;
	for( int hh=h-deltaIntZ ; hh<=h+deltaIntZ ; hh++ )
	  for( int ii=i-deltaIntX ; ii<=i+deltaIntX ; ii++ )
	    for( int jj=j-deltaIntY ; jj<=j+deltaIntY ; jj++ )
	      if( ii>=0 && jj>=0 && hh>=0 && 
		  ii<xSize && jj<ySize && hh<zSize ) { 
		double d=std::sqrt( (hh-h)*(hh-h)*zFac
			       +(ii-i)*(ii-i)*xFac
			       +(jj-j)*(jj-j)*yFac );
		if( d<=radius ) { 
		  double weight = (1.-d/radius);
		  value += weight*pix(hh,ii,jj);
		  norm += weight;
		}
	      }
	if( norm<=0 || value<0. ) {
	  std::cerr << "Stack::smothen: Wrong weight given...\n";
	  exit(-1);
	}
	latticeData[h][i][j] = value/norm;
      }
  
  //Copy the values
  for( int h=0 ; h<zSize ; h++ )
    for( int i=0 ; i<xSize ; i++ )
      for( int j=0 ; j<ySize ; j++ )
	setPix(h,i,j,latticeData[h][i][j]);
}

//!Smothens each image by itself using the smothen function in the image class
/*! Note that the supplied radius is in micrometer which is converted
  to pixel radii in the image function...*/
void Stack::imageSmothen(double weight, double radius) {

  if( weight<0. ) {
    std::cerr << "Stack::smothen: Wrong weight given...\n";
    exit(-1);
  }
  
  if( xScale() != yScale() ) 
    std::cerr << "Stack::imageSmothen() Warning! xScale and yScale "
	 << "not equal. Using xScale...\n"; 

  double newRadius = radius/xScale();//assuming x- and y-scale are the same
  
  register int zSize=D();
  for(int h=0 ; h<zSize ; h++ )
    pix_[h].smothen(weight,newRadius);
}

//!Smothens each image by itself using the triSmothen from the image class
/*! Note that the supplied radius is in micrometer which is converted
  to pixel radii in the image function...*/
void Stack::imageTriSmothen(double radius) {

  if( xScale() != yScale() ) 
    std::cerr << "Stack::imageSmothen() Warning! xScale and yScale "
	 << "not equal. Using xScale...\n"; 
  
  double pixelRadius = radius/xScale();//assuming x- and y-scale are the same
  
  register int zSize=D();
  for(int h=0 ; h<zSize ; h++ )
    pix_[h].triSmothen(pixelRadius);
}

//! Checks if all pixels are between lR-hR and returns 1 if they are (else 0)
int Stack::inRange(int lowRange,int highRange) {
  
  for( int h=0 ; h<D() ; h++ )
    for( int i=0 ; i<H() ; i++ )
      for( int j=0 ; j<W() ; j++ ) {
	double aux = pix(h,i,j);
	if( aux<lowRange || aux>highRange )
	  return 0; //out of range... 
      }
  return 1; //in range...
}

//! Changes all pixel values so that they are in 0-255, by normalising
void Stack::putInRange(int lowRange,int highRange) {
  
  int deltaRange=highRange-lowRange;

  double min,max;

  for( int h=0 ; h<D() ; h++ )
    for( int i=0 ; i<H() ; i++ )
      for( int j=0 ; j<W() ; j++ ) {
	if( (h==0 && i==0 && j==0) || pix(h,i,j)<min )
	  min = pix(h,i,j);
	if( (h==0 && i==0 && j==0) || pix(h,i,j)>max )
	  max = pix(h,i,j);
      }
  
  double delta = max-min;
  
  for( int h=0 ; h<D() ; h++ )
    for( int i=0 ; i<H() ; i++ )
      for( int j=0 ; j<W() ; j++ ) {
	double aux = lowRange + deltaRange * (pix(h,i,j)-min)/delta;
	setPix( h,i,j, aux );
    }
}

//! Inverts all pixels from p to pp=255-p
void Stack::invert() {
  
  if( !(inRange()) ) {
    std::cerr << "Stack::invert() "
	 << "Warning normalizing before inverting.\n";
    putInRange();
  }
  
  for( int h=0 ; h<D() ; h++ )
    for( int i=0 ; i<H() ; i++ )
      for( int j=0 ; j<W() ; j++ )
	setPix( h,i,j,255-pix(h,i,j) );
}

void Stack::flagToBoa(std::vector< std::vector< std::vector<int> > > &flag,
		      std::vector< std::vector< std::vector<int> > > &boa) {

  //Find maximal flag value
  int max=0;
  for( int h=0 ; h<D() ; h++ )
    for( int i=0 ; i<H() ; i++ )
      for( int j=0 ; j<W() ; j++ )
	if( flag[h][i][j]>max )
	  max=flag[h][i][j];
// 	else if( flag[h][i][j]<1 ) {
// 	  std::cerr << "Stack::flagToBoa - flag value zero or neg!\n";
// 	  exit(-1);
// 	}

  boa.resize( max );
  std::vector<int> tmp(3);
  //Populate the basins of attraction
  for( int h=0 ; h<D() ; h++ ) {
    tmp[0]=h;
    for( int i=0 ; i<H() ; i++ ) {
      tmp[1]=i;
      for( int j=0 ; j<W() ; j++ ) {
	tmp[2]=j;
	if( flag[h][i][j]>0 )
	  boa[ flag[h][i][j]-1 ].push_back(tmp);
      }
    }
  }
  std::vector< std::vector< std::vector<int> > > boaTmp;
  for( int i=0 ; i<boa.size() ; i++ )
    if( boa[i].size() )
      boaTmp.push_back(boa[i]);
  boa = boaTmp;
}

//!Looks for spacial "holes" in the given data with intensity>threshold
int Stack::findHoles( std::vector< std::vector<int> > & data, 
		      std::vector< std::vector<int> > & hole, double R, 
		      int intensityThreshold ) {
  
  std::vector<int> tmp(3);
  //Find mins and max' in all directions
  int xMin,yMin,zMin,xMax,yMax,zMax;
  
  zMin = zMax = data[0][0];
  xMin = xMax = data[0][1];
  yMin = yMax = data[0][2];
  for( int i=1 ; i<data.size() ; i++ ) {
    if( data[i][0]<zMin )
      zMin = data[i][0];
    if( data[i][0]>zMax )
      zMax = data[i][0];
    if( data[i][1]<xMin )
      xMin = data[i][1];
    if( data[i][1]>xMax )
      xMax = data[i][1];
    if( data[i][2]<yMin )
      yMin = data[i][2];
    if( data[i][2]>yMax )
      yMax = data[i][2];
  }


  //double x0=97,y0=97,z0=13,r0=35, z0Hat=18;
  double x0=47./xScale(),y0=47./yScale(),z0=13./zScale(),r0=34./xScale()
    , z0Hat=13./zScale();
  for( int z=zMin ; z<=zMax ; z++ )
    for( int x=xMin ; x<=xMax ; x++ )
      for( int y=yMin ; y<=yMax ; y++ )
	if( z>=z0Hat && 
	    std::sqrt( (z-z0)*(z-z0)
		  +(x-x0)*(x-x0)
		  +(y-y0)*(y-y0) ) <r0 ) {
	  
	  double minDist,nextMinDist;
	  for( int n=0 ; n<data.size() ; n++ ) {
	    double d = std::sqrt( double( (data[n][0]-z)*(data[n][0]-z)
			     +(data[n][1]-x)*(data[n][1]-x)
			     +(data[n][2]-y)*(data[n][2]-y) ) );
	    if( n==0 ) 
	      minDist=d;
	    else if( n==1 ) {
	      if( d<minDist ) {
		nextMinDist=minDist;
		minDist = d;
	      }
	      else 
		nextMinDist=d;		
	    }
	    else if( d<nextMinDist ) {
	      if( d<minDist ) {
		nextMinDist=minDist;
		minDist = d;
	      }
	      else
		nextMinDist = d;
	    } 
	  }
	  //std::cerr << z << " " << D() << "\t"
	  //   << x << " " << H() << "\t"
	  //   << y << " " << W() << "\n";
	  if( minDist>R && pix(z,x,y)>intensityThreshold 
	      && nextMinDist-minDist<0.1*R ) {
	    tmp[0]=z;tmp[1]=x;tmp[2]=y;
	    hole.push_back( tmp );
	  }
	}

  return hole.size();
}

//!Adjust the cell positions by using maxima in derivatives in x and y.
/*!Caveat: uses old pixel values as input...and data in z,x,y,...-format*/
void Stack::adjustCenters( std::vector< std::vector<int> > &cellCenter, 
			   std::vector< std::vector<int> > &cellCenterAdjusted,
			   int maxRadius) {
  
  int K = cellCenter[0].size();
  int N = cellCenter.size();
  std::vector<int> tmp(K);
  cellCenterAdjusted.resize(N,tmp);
  
  for( int n=0 ; n<N ; n++ ) {
    int zC,xC,yC;
    int xLow,xHigh,yLow,yHigh;
    cellCenterAdjusted[n] = cellCenter[n];
    int change=1;
    std::vector< std::vector<int> > xyTmp(1,tmp);
    xyTmp[0][0] = cellCenter[n][1];
    xyTmp[0][1] = cellCenter[n][2];
    while( change ) {
      change=0;
      //Try the x direction
      zC = cellCenterAdjusted[n][0];
      xC = cellCenterAdjusted[n][1];
      yC = cellCenterAdjusted[n][2];
      double min,max,diff=0.;
      //Up
      for( int x=xC ; x>=xC-maxRadius ; x-- ) {
	if( x>=0 && x<H()-1 ) {
	  diff = pix(zC,x+1,yC) - pix(zC,x,yC); 
	  if( x==xC || diff>max ) {
	    xLow = x;
	    max = diff;
	  }
	}
      }
      diff=0.;
      //Down
      for( int x=xC ; x<=xC+maxRadius ; x++ ) {
	if( x>=0 && x<H()-1 ) {
	  diff = pix(zC,x+1,yC) - pix(zC,x,yC); 
	  if( x==xC || diff<min ) {
	    xHigh = x;
	    min = diff;
	  }
	}
      }
      int newXC = int( 0.5*(1+xHigh+xLow) );
      //if( newXC>xC ) { xC++;change++; /*std::cerr << n << " moved (x) +1\n";*/}
      //else if( newXC<xC ) { xC--;change++; }
      if( newXC != xC ) {
	xC = newXC;
	change++;
      }

      diff=0.;
      //Left
      for( int y=yC ; y>=yC-maxRadius ; y-- ) {
	if( y>=0 && y<W()-1 ) {
	  diff = pix(zC,xC,y+1) - pix(zC,xC,y); 
	  if( y==yC || diff>max ) {
	    yLow = y;
	    max = diff;
	  }
	}
      }
      diff=0.;
      //Right
      for( int y=yC ; y<=yC+maxRadius ; y++ ) {
	if( y>=0 && y<W()-1 ) {
	  diff = pix(zC,xC,y+1) - pix(zC,xC,y); 
	  if( y==yC || diff<min ) {
	    yHigh = y;
	    min = diff;
	  }
	}
      }
      int newYC = int( 0.5*(1+yHigh+yLow) );
      //if( newYC>yC ) { yC++;change++; /*std::cerr << n << " moved (y) +1\n";*/ }
      //else if( newYC<yC ) { yC--;change++; }
      if( newYC != yC ) {
	yC = newYC;
	change++;
      }
      
      if( change ) {
	cellCenterAdjusted[n][1] = xC; 
	cellCenterAdjusted[n][2] = yC;
      }
      //Check if we have been here before to avoid eternal loop
      for( int i=0 ; i<xyTmp.size() ; i++ )
	if( xyTmp[i][0]==xC && xyTmp[i][1]==yC ) 
	  change=0;
      
      tmp[0] = xC;tmp[1] = yC;
      xyTmp.push_back(tmp);
    }
    double dX = cellCenterAdjusted[n][1]-cellCenter[n][1];
    double dY = cellCenterAdjusted[n][2]-cellCenter[n][2];
    double d = std::sqrt( dX*dX+dY*dY );
    //std::cerr << n << " " << cellCenter[n][1] << " " << cellCenter[n][2] 
    //<< "\t" << dX << " " << dY << "\t" << d << "\n";

    //Add the maximas (border points) for analysis puposes...
    tmp[0]=zC;
    for( int delta=xLow ; delta<=xHigh ; delta++ ) {
      tmp[1]=delta;tmp[2]=yC;
      cellCenterAdjusted.push_back(tmp);
    }
    //tmp[1]=xHigh;tmp[2]=yC;
    //cellCenterAdjusted.push_back(tmp);
    for( int delta=yLow ; delta<=yHigh ; delta++ ) {
      tmp[1]=xC;tmp[2]=delta;
      cellCenterAdjusted.push_back(tmp);
    }
    //tmp[1]=xC;tmp[2]=yHigh;
    //cellCenterAdjusted.push_back(tmp);
  }
}

int Stack::findEdges(std::vector< std::vector< std::vector<int> > > &hijEdges,
		     double threshold,int neighborhood) {

//    std::vector<int> tmp(2);
//    if( ijEdges.size() )
//      ijEdges.resize(0);

//    for( int i=neighborhood ; i<H()-neighborhood ; i++ )
//      for( int j=neighborhood ; j<W()-neighborhood ; j++ ) {
      
//        double xDeriv=0.,yDeriv=0.;;
//        for( int n=0 ; n<neighborhood ; n++ ) {
//  	xDeriv += pix(i,j+n+1) - pix(i,j-n);
//  	yDeriv += pix(i+n+1,j) - pix(i-n,j);
//        }
//        xDeriv = fabs(xDeriv)/pix(i,j);
//        yDeriv = fabs(yDeriv)/pix(i,j);
//        double deriv = xDeriv+yDeriv;
//        if( deriv>threshold ) {
//  	int n=ijEdges.size();
//  	ijEdges.push_back( tmp );
//  	ijEdges[n][0] = i;
//  	ijEdges[n][1] = j;
//        }
//      }

//    return ijEdges.size();
}


//int Stack::find2ndDer(std::vector< std::vector<int> > &ijPixels,double threshold=0.,
//	      int neighborhood=1) {

//    std::vector<int> tmp(2);
//    if( ijPixels.size() )
//      ijPixels.resize(0);

//    for( int i=neighborhood ; i<H()-neighborhood ; i++ )
//      for( int j=neighborhood ; j<W()-neighborhood ; j++ ) {
      
//        double xy2Deriv=0.;
//        int count=0;
//        for( int n=1 ; n<=neighborhood ; n++ ) {
//  	xy2Deriv += pix(i,j+n) + pix(i,j-n) + pix(i+n,j) + pix(i-n,j);
//  	count += 4;
//        }
//        xy2Deriv -= count*pix(i,j);

//        xy2Deriv /= pix(i,j);

//        if( xy2Deriv>threshold ) {
//  	int n=ijPixels.size();
//  	ijPixels.push_back( tmp );
//  	ijPixels[n][0] = i;
//  	ijPixels[n][1] = j;
//        }
//      }
  
//    return ijPixels.size();
//}

//!Calculates the 2nd derivative at each pixel
/*! The neighborhood flag is how many pixels from the original one
  that is used to calculate the derivative. The normFlag is used if
  one wants to norm with the pixel value.*/
void Stack::ScndDer( int neighborhood , int normFlag ) {

//    std::vector< std::vector<double> > tmp( H() );
//    for( int i=0 ; i<tmp.size() ; i++ )
//      tmp[i].resize( W() );

//    // Calclulate the 2nd derivatives 
//    for( int i=neighborhood ; i<H()-neighborhood ; i++ )
//      for( int j=neighborhood ; j<W()-neighborhood ; j++ ) {
//        double xy2Deriv=0.;
//        int count=0;
//        for( int n=1 ; n<=neighborhood ; n++ ) {
//  	xy2Deriv += pix(i,j+n) + pix(i,j-n) + pix(i+n,j) + pix(i-n,j);
//  	count += 4;
//        }
//        xy2Deriv -= count*pix(i,j);
      
//        if( normFlag ) 
//  	xy2Deriv /= pix(i,j); //If norm to pixel value wanted...
      
//        tmp[i][j] = xy2Deriv;
//      }
  
//    //Copy them into the pixels
//    for( int i=0 ; i<H() ; i++ )
//      for( int j=0 ; j<W() ; j++ )
//        setPix( i,j,tmp[i][j] );
}
