/**
 * Filename     : stack.h
 * Description  : A class representing a stack of images as a 3D lattice
 * Author(s)    : Henrik Jonsson (henrik@caltech.edu)
 * Organization : California Institute of Technology
 * Created      : January 2003
 * Revision     : $Id$
 *
 * Copyright 2003, California Institute of Technology.
 * ALL RIGHTS RESERVED.  U.S. Government Sponsorship acknowledged.
 */
#ifndef STACK_H
#define STACK_H

#include<tiffio.h>
#include<vector>
#include<string>
#include<cmath>
#include"image.h"

//! Stores a stack of images as a 3D lattice of pixel values.
/*! .*/
class Stack{
  
  std::vector<Image> pix_;
  std::vector<double> scale_;
  std::string id_;
  
 public:

  Stack(double zScale=1.,double xScale=1.,double yScale=1.);
  Stack( const Stack & stackCopy );
  Stack( std::vector< std::vector< std::vector<double> > > & pixValue,
	 double zScale=1.,double xScale=1.,double yScale=1. );
  Stack( int dValue,int hValue,int wValue,
	 double zScale=1.,double xScale=1.,double yScale=1. );
  Stack( char *directory,double zScale=1.,double xScale=1.,double yScale=1.,
	 int flag=0 );
  Stack( std::string directory,double zScale=1.,double xScale=1.,double yScale=1.,
	 int flag=0 );
  
  ~Stack();
  
  Stack & operator=( const Stack & stackCopy );
  
  // Get values
  inline double pix(int h,int i,int j) const;
  inline int D() const;
  inline int H() const;
  inline int W() const;
  inline std::vector<double> scale() const;
  inline double scale(int dim) const;
  inline double zScale() const;
  inline double xScale() const;
  inline double yScale() const;
  inline std::string id() const;

  // Set values
  inline void setPix(int h,int i,int j,double value=0.);
  inline void setD(int value=0);
  inline void setH(int value=0);
  inline void setW(int value=0);
  inline void setScale(int dim,double value);
  inline void setScale(std::vector<double> &value);
  inline void setZScale(double value);
  inline void setXScale(double value);
  inline void setYScale(double value);
  inline void setId(char *value);
  inline void setId(std::string value);

  //Resizing the z-axis grid (interpolating)
  void interpolatePolynomialZ(double newZscale,int polDeg=1); 
  double polint(std::vector<double> &xa, std::vector<double> &ya, double x);


  // Reading and writing of stack files
  int readStack( char *directory, int flag=0 );
  int readStack( std::string directory, int flag=0 );
  int readStackTiff( char *directory, int zOrder=1 );
  int readStackTiff( std::string directory );
  
  int writeStack( char *directory, char* name="", int flag=0 );
  int writeStack( std::string directory, std::string name="", int flag=0 );
  int writeStackTiff( char *directory, char *name="" );
  int writeStackTiff( std::string directory, std::string name="" );
  int writeXStackTiff( char *directory, char *name="", int x0=-1, int y0=-1, 
		       int r=-1 );
  int writeYStackTiff( char *directory, char *name="", int x0=-1, int y0=-1, 
		       int r=-1 );
  int writeRStackTiff( char *directory, char *name="", int x0=-1, int y0=-1, 
		       int r=-1, int deltaPhi=5 );
  int writeStackTiffWithRedPixels( char *directory, 
				   std::vector< std::vector<int> > 
				   &redPixels, char* name="" );
  int writeStackTiffWithRedBluePixels( char *directory, 
				       std::vector< std::vector<int> > 
				       &redPixels,
				       std::vector< std::vector<int> > 
				       &bluePixels, char *name="" );
  int writeStackTiffWithRandomColors(char *directory,
				     std::vector< std::vector< 
				     std::vector<int> > > &boa,
				     char* name="");
  //simple processing functions
  //double neighborhoodMean(int rowPixel,int colPixel,int threshold=1);
  void smothen(double weight=1., double radius=1.5);
  void triSmothen(double radius=1.5);
  void imageSmothen(double weight=1.,double radius=1.5);
  void imageTriSmothen(double radius=1.5);
  int inRange(int lowRange=0,int highRange=255);
  void putInRange(int lowRange=0,int highRange=255);
  void invert();
  
  void flagToBoa(std::vector< std::vector< std::vector<int> > > &flag,
		 std::vector< std::vector< std::vector<int> > > &boa);

  int findHoles( std::vector< std::vector<int> > & data, 
		 std::vector< std::vector<int> > & hole, double R=2.5, 
		 int intensityThreshold=20 );
    


  // Functions for finding different behaviors
  int findMaximaByLocalSearch(std::vector< std::vector<int> > &ijMin, 
			      std::vector< std::vector< 
			      std::vector<int> > > &flag,
			      double valThreshold=0.,
			      int valThresholdFlag=0,int threshold=1);
  int findMaximaByLocalSearch(std::vector< std::vector<int> > &ijMin, 
			      std::vector< std::vector< 
			      std::vector<int> > > &flag,
			      std::vector< std::vector<int> > &bg,
			      double valThreshold=0.,
			      int valThresholdFlag=0,int threshold=1);
  void addPlateauNeighborRecursive(double value,int h,int i,int j,
				   std::vector< std::vector< 
				   std::vector<int> > > &tmpFlag,
				   std::vector< std::vector<int> > 
				   &walkTmp);

  int imageFindMaximaByLocalSearch(std::vector< std::vector<int> > &ijMin, 
				   double valThreshold=0.,
				   int valThresholdFlag=0,int threshold=1);
  int findEdges(std::vector< std::vector< std::vector<int> > > &ijEdges,
		double threshold=10.,int neighborhood=1);
  void ScndDer(int neighborhood=1,int normFlag=0);
  void adjustCenters( std::vector< std::vector<int> > &cellCenter, 
		      std::vector< std::vector<int> > &cellCenterAdjusted,
		      int maxRadius=3);


};

//! Returns the pixel at position h,i,j
inline double Stack::pix(int h,int i,int j) const { return pix_[h].pix(i,j); }
//! Returns the number of images (num z points)
inline int Stack::D() const { return pix_.size(); }
//! Returns the row numbers in the matrix
inline int Stack::H() const { return pix_[0].H(); }
//! Returns the columns numbers in the matrix
inline int Stack::W() const { return pix_[0].W(); }
//! Returns the scales in a vector of values (zScale,x,y)
inline std::vector<double> Stack::scale() const { return scale_; }
//! Returns the scale in given dimension (0,1,2)=(z,x,y)
inline double Stack::scale(int dim) const { return scale_[dim]; }
//! Returns the scale in z-dimension
inline double Stack::zScale() const { return scale_[0]; }
//! Returns the scale in x-dimension
inline double Stack::xScale() const { return scale_[1]; }
//! Returns the scale in y-dimension
inline double Stack::yScale() const { return scale_[2]; }
//! Returns the identification string
inline std::string Stack::id() const { return id_; }

inline void Stack::setPix(int h,int i,int j,double value) {
  pix_[h].setPix(i,j,value);}
inline void Stack::setD(int value) { pix_.resize( value ); }
inline void Stack::setH(int value) { 
  for(int h=0 ; h<D() ; h++ )
    pix_[h].setH( value ); }
inline void Stack::setW(int value) {
  for(int h=0 ; h<D() ; h++ )
    pix_[h].setW( value ); 
}
inline void Stack::setScale(int dim,double value) { scale_[dim]=value; }
inline void Stack::setScale(std::vector<double> &value) { scale_ = value; }
inline void Stack::setZScale(double value) { scale_[0]=value; }
inline void Stack::setXScale(double value) { scale_[1]=value; }
inline void Stack::setYScale(double value) { scale_[2]=value; }

inline void Stack::setId(char *value) { id_ = std::string(value); }
inline void Stack::setId(std::string value) { id_ = value; }

#endif
