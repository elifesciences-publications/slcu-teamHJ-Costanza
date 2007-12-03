/**
 * Filename     : image.h
 * Description  : A class representing an image as a matrix
 * Author(s)    : Henrik Jonsson (henrik@caltech.edu)
 * Organization : California Institute of Technology
 * Created      : August 2002
 * Revision     : $Id$
 *
 * Copyright 2002, California Institute of Technology.
 * ALL RIGHTS RESERVED.  U.S. Government Sponsorship acknowledged.
 */
#ifndef IMAGE_H
#define IMAGE_H

#include<tiffio.h>
#include<vector>
#include<string>
#include<iostream>
//! Stores a image as a matrix of pixel values.
/*! .*/
class Image{

  std::vector< std::vector<double> > pix_;
  std::string id_;

public:

  Image();
  Image( const Image & imageCopy );
  Image( std::vector< std::vector<double> > & pixValue );
  Image( int hValue,int wValue );
  Image( char *file,int flag=0);
  Image( std::string file,int flag=0);

  ~Image();

  Image & operator=( const Image & imageCopy );

  // Get values
  inline double pix(int i,int j) const;
  inline int H() const;
  inline int W() const;
  inline std::string id() const;

  // Set values
  inline void setPix(int i,int j,double value);
  inline void setH(int value);
  inline void setW(int value);
  inline void setId(char *value);
  inline void setId(std::string value);

  // Reading and writing of image files
  int readImage( char *file, int flag);
  int readImage( std::string file, int flag);
  int readImageTiff( char *file );
  int readImageTiff( std::string file );
  
  int writeImage( char *file, int flag);
  int writeImage( std::string file, int flag);
  int writeImageTiff( char *file );
  int writeImageTiff( std::string file );
  int writeImageTiffWithRedPixels( char *file, 
				   std::vector< std::vector<int> > &redPixels );
  int writeImageTiffWithRedCircles( char *file, 
				    std::vector< std::vector<int> > &redPixels,
				    std::vector<double> &radius );
  
  int writeImageTiffWithRedBluePixels( char *file, 
				       std::vector< std::vector<int> > &redPixels,
				       std::vector< std::vector<int> > &bluePixels );
  int writeImageTiffWithRedGreenBluePixels( char *file, 
					    std::vector< std::vector<int> > &redPixels,
					    std::vector< std::vector<int> > &greenPixels,
					    std::vector< std::vector<int> > &bluePixels);

  int writeImageTiffWithRandomColors( char *file, 
				      std::vector< std::vector< std::vector<int> > > 
				      &clusters );
  
  int writeImageTiffWithDefinedColors( char *file, 
				       std::vector< std::vector< 
				       std::vector<int> > > &clusters,
				       std::vector< std::vector<char> > 
				       &color);
    
  int writeImageTiffWithDefinedColors( const char *file, 
				       std::vector< std::vector< 
				       std::vector<int> > > &clusters,
				       std::vector<double> &color);
    
  //simple processing functions
  double neighborhoodMean(int rowPixel,int colPixel,int threshold);
  void smothen(double weight,double radius);
  void triSmothen(double radius);
  int inRange(int lowRange,int highRange);
  void putInRange(int lowRange,int highRange);
  void invert();
  double min();
  double max();


  void adjustCenters( std::vector< std::vector<int> > &cellCenter, 
		      std::vector< std::vector<int> > &cellCenterAdjusted,
		      double maxRadius);
  void adjustCenters4( std::vector< std::vector<int> > &cellCenter, 	
	       std::vector< std::vector<int> > &cellCenterAdjusted,
		       std::vector<double> &radius,
		       double maxRadius);
  void compareExpressionCircles( Image &I2,std::vector< std::vector<int> > &cellCenter,
				 std::vector<double> &radius, 
				 std::vector< std::vector<double> > &stat );
  void cellCenterFromPixels( std::vector< std::vector< std::vector<int> > > &pixels,
			     std::vector< std::vector<int> > &cellCenter);
  void polarization( Image &gfpI, std::vector< std::vector< std::vector<int> > > &pixels,
		     std::vector< std::vector<int> > &cellCenter,
		     std::vector< std::vector<double> > &polarization);
  
  void comparePolarizationPixels( Image &I2,std::vector< std::vector<int> > &p,
				  std::vector< std::vector<int> > &pixels,
				  std::vector<double> &statTmp );
  
  void findPixelNeighborsRectangle(double r,std::vector< std::vector<int> > &p,
				   std::vector< std::vector<int> > &pixels);
  
  // Functions for finding different behaviors
  int findMinimaByGradientDescent(std::vector< std::vector<int> > &ijMin, 
				  double valThreshold=0.0,
				  int valThresholdFlag=0,int threshold=1);
  int findMaximaByGradientDescent(std::vector< std::vector<int> > &ijMin, 
				  double valThreshold=0.0,
				  int threshold=1);
  int findMaximaByGradientDescent(std::vector< std::vector<int> > &ijMin, 
				  std::vector< std::vector< 
				  std::vector<int> > > &boa,
				  double valThreshold=0.0,
				  int threshold=1);
  int findMaximaByGradientDescent(std::vector< std::vector<int> > &ijMin, 
				  std::vector< std::vector< 
				  std::vector<int> > > &boa,
				  std::vector< std::vector<int> > &bg, 
				  double valThreshold=0.0,
				  int threshold=1);
  int findEdges(std::vector< std::vector<int> > &ijEdges,double threshold,
		 int neighborhood);
  int find2ndDer(std::vector< std::vector<int> > &ijPixels,double threshold,
		 int neighborhood);
  void firstDer(int neighborhood);
  void scndDer(int neighborhood,int normFlag);

  void zeroPixels( std::vector< std::vector<int> > &zero );
  int setPixels( std::vector< std::vector<int> > &zero, double value );

};

//! Returns the pixel at position i,j
inline double Image::pix(int i,int j) const { return pix_[i][j]; }
//! Returns the row numbers in the matrix
inline int Image::H() const { return pix_.size(); }
//! Returns the columns numbers in the matrix
inline int Image::W() const { return pix_[0].size(); }
//! Returns the identification std::string
inline std::string Image::id() const { return id_; }

inline void Image::setPix(int i,int j,double value=0.) {pix_[i][j] = value;}
inline void Image::setH(int value=0) { pix_.resize( value ); }
inline void Image::setW(int value=0) {
  for(int i=0 ; i<H() ; i++ )
    pix_[i].resize( value ); 
}

inline void Image::setId(char *value) { id_ = std::string(value); }
inline void Image::setId(std::string value) { id_ = value; }

#endif
