/**
 * Filename     : image.cc
 * Description  : Functions for the Image Class defined in image.h
 * Author(s)    : Henrik Jonsson (henrik@caltech.edu)
 * Organization : California Institute of Technology
 * Created      : August 2002
 * Revision     : $Id$
 *
 * Copyright 2002, California Institute of Technology.
 * ALL RIGHTS RESERVED.  U.S. Government Sponsorship acknowledged.
 */

#include<iostream>
#include"image.h"
#include<cmath>

Image::Image() {
  std::cerr << "Image::Image()\n";
}

Image::Image(const Image & imageCopy) {

  setH( imageCopy.H() );
  setW( imageCopy.W() );
  for(int i=0 ; i<H() ; i++ )
    for(int j=0 ; j<W() ; j++ )
      setPix(i,j,imageCopy.pix(i,j));
  setId( imageCopy.id() );
}

Image::Image(std::vector< std::vector<double> > & pixValue) {
  
  setH( pixValue.size() );
  setW( pixValue[0].size() );
  for(int i=0 ; i<H() ; i++ )
    for(int j=0 ; j<W() ; j++ )
      setPix(i,j,pixValue[i][j]);
}

Image::Image( int hValue, int wValue ) { 
  setH( hValue );
  setW( wValue );
}

Image::Image( char *file,int flag ) {

  readImage(file,flag);
}

Image::Image( std::string file,int flag ) {
  readImage(file,flag);
}

Image::~Image() {

}

Image & Image::operator=( const Image & imageCopy ) {

  setH( imageCopy.H() );
  setW( imageCopy.W() );
  for(int i=0 ; i<H() ; i++ )
    for(int j=0 ; j<W() ; j++ )
      setPix(i,j,imageCopy.pix(i,j));
  setId( imageCopy.id() );

  return (*this);
}

int Image::readImage( char *file, int flag=0 ) {

  if( flag==0 ) 
    return readImageTiff(file);
  else {
    std::cerr << "Image::readImage(): "
	 << "Only reading from tiff files (flag=0) implemented sofar!\n";
    exit(0);
  }
}

int Image::readImage( std::string file, int flag=0 ) {

  if( flag==0 ) 
    return readImageTiff(file);
  else {
    std::cerr << "Image::readImage(): "
	 << "Only reading from tiff files (flag=0) implemented sofar!\n";
    exit(0);
  }
}

#define TIFFGetR(abgr)      ((abgr) & 0xff)
#define TIFFGetG(abgr)      (((abgr) >> 8) & 0xff)
#define TIFFGetB(abgr)      (((abgr) >> 16) & 0xff)
#define TIFFGetA(abgr)      (((abgr) >> 24) & 0xff)
int Image::readImageTiff( char *file ) {

  TIFF* tif = TIFFOpen(file, "r");
  if (tif) {
    uint32 w, h;
    size_t npixels;
    uint32* raster;

    TIFFGetField(tif, TIFFTAG_IMAGEWIDTH, &w);
    TIFFGetField(tif, TIFFTAG_IMAGELENGTH, &h);
        
    setH( h );
    setW( w );

    npixels = w * h;
    raster = (uint32*) _TIFFmalloc(npixels * sizeof (uint32));
    if (raster != NULL) {
      if( TIFFReadRGBAImage(tif, w, h, raster, 0) ) {

        //Convert the pixel values 
	//Caveat: Order of rows? Must read in this order to get it correct...
        int c=0;
        for( int i=H()-1 ; i>=0 ; i-- )
          for( int j=0 ; j<W() ; j++ ) {
	    double aux = ( TIFFGetR( raster[c] ) +
	      TIFFGetG( raster[c] ) +
	      TIFFGetB( raster[c] ) )/3.;
            setPix(i,j,aux);
            c++;
          }
      }
      _TIFFfree(raster);
    }
    TIFFClose(tif);
  }
  
  setId( file );
}

int Image::readImageTiff( std::string file ) {
  char *tmp = const_cast<char*>( file.c_str() );
  return readImageTiff( tmp );
}
#undef TIFFGetR
#undef TIFFGetG
#undef TIFFGetB
#undef TIFFGetA

int Image::writeImage( char *file, int flag=0 ) {

  if( flag==0 ) 
    return writeImageTiff(file);
  else {
    std::cerr << "Image::writeImage(): "
	 << "Only writing to tiff files (flag=0) implemented sofar!\n";
    exit(0);
  }
}

int Image::writeImage( std::string file, int flag=0 ) {

  if( flag==0 ) 
    return writeImageTiff(file);
  else {
    std::cerr << "Image::writeImage(): "
	 << "Only writing to tiff files (flag=0) implemented sofar!\n";
    exit(0);
  }
} 

int Image::writeImageTiff( char *file ) {

  TIFF *output;
  uint32 width, height;
  char *raster;
  int samplesPerPixel = 3; //R,G,B
  //int samplesPerPixel = 4; //R,G,B,A

  // Open the output image
  if((output = TIFFOpen(file, "w")) == NULL){
    std::cerr << "Could not open outgoing image\n";
    exit(42);
  }

  // We need to know the width and the height before we can malloc
  width = W();
  height = H();
  
  if((raster = (char *) malloc(sizeof(char) * width * height * samplesPerPixel)) == NULL){
    std::cerr << "Could not allocate enough memory\n";
    exit(42);
  }
  
  // Converting the picture matrix into the raster...
  int c=0;
  for( int i=0 ; i<H() ; i++ ) 
    for( int j=0 ; j<W() ; j++ ) {
      char tmp = char( pix(i,j) );
      for( int a=0 ; a<3 ; a++ )
	raster[c++] = tmp;//equal colors to each pixels
      if( samplesPerPixel==4 )
	raster[c++] = 255;//set alpha channel to 255 (not transparent...???)
    }
  
  // Write the tiff tags to the file
  TIFFSetField(output, TIFFTAG_IMAGEWIDTH, width);
  TIFFSetField(output, TIFFTAG_IMAGELENGTH, height);
  //TIFFSetField(output, TIFFTAG_COMPRESSION, COMPRESSION_DEFLATE);
  TIFFSetField(output, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
  TIFFSetField(output, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB);
  TIFFSetField(output, TIFFTAG_BITSPERSAMPLE, 8);
  TIFFSetField(output, TIFFTAG_SAMPLESPERPIXEL, samplesPerPixel);

  // Actually write the image
  if(TIFFWriteEncodedStrip(output, 0, raster, 
			   width * height * samplesPerPixel) == 0){
    std::cerr << "Could not write image\n";
    exit(42);
  }
  
  TIFFClose(output);
}

int Image::writeImageTiff( std::string file ) {
  writeImageTiff( const_cast<char*>( file.c_str() ) );
}

int Image::writeImageTiffWithRandomColors( char *file, 
					   std::vector< std::vector< 
					   std::vector<int> > > &clusters ) {
  TIFF *output;
  uint32 width, height;
  char *raster;
  int samplesPerPixel = 3; //R,G,B
  //int samplesPerPixel = 4; //R,G,B,A
  
  // Open the output image
  if((output = TIFFOpen(file, "w")) == NULL){
    std::cerr << "Could not open outgoing image\n";
    exit(42);
  }
  
  // We need to know the width and the height before we can malloc
  width = W();
  height = H();
  int pixelNum = width * height;
  if((raster = (char *) malloc(sizeof(char) * width * height * 
			       samplesPerPixel)) == NULL){
    std::cerr << "Could not allocate enough memory\n";
    exit(42);
  }
  // Converting the picture matrix into the raster...
  int c=0;
  for( int i=0 ; i<H() ; i++ ) 
    for( int j=0 ; j<W() ; j++ ) {
      char tmp = char( pix(i,j) );
      for( int a=0 ; a<3 ; a++ )
	raster[c++] = tmp;//equal colors to each pixels
      if( samplesPerPixel == 4 )
	raster[c++] = 255;//set alpha channel to 255 (not transparent...???)
    }
  //Change given points to colored ones...
  for( int n=0 ; n<clusters.size() ; n++ ) {
    //define a random color
    char red=char( random()*254 );
    char green=char( random()*254 );
    char blue=char( random()*254 );
    for( int i=0 ; i<clusters[n].size() ; i++ ) {
      int pixel = clusters[n][i][0]*W() + clusters[n][i][1]; 
      if( pixel<pixelNum ) {
	int rasterPoint0 = pixel*samplesPerPixel; 
	raster[rasterPoint0]=red;//char(254);
	raster[rasterPoint0+1]=green;//char(0);
	raster[rasterPoint0+2]=blue;//char(0);
      }
    }
  }
  // Write the tiff tags to the file
  TIFFSetField(output, TIFFTAG_IMAGEWIDTH, width);
  TIFFSetField(output, TIFFTAG_IMAGELENGTH, height);
  //TIFFSetField(output, TIFFTAG_COMPRESSION, COMPRESSION_DEFLATE);
  TIFFSetField(output, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
  TIFFSetField(output, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB);
  TIFFSetField(output, TIFFTAG_BITSPERSAMPLE, 8);
  TIFFSetField(output, TIFFTAG_SAMPLESPERPIXEL, samplesPerPixel);

  // Actually write the image
  if(TIFFWriteEncodedStrip(output, 0, raster, 
			   width * height * samplesPerPixel) == 0){
    std::cerr << "Could not write image\n";
    exit(42);
  }
  
  TIFFClose(output);
}

int Image::writeImageTiffWithDefinedColors( char *file, 
					    std::vector< std::vector< 
					    std::vector<int> > > &clusters,
					    std::vector< std::vector<char> > 
					    &color) {
  if( clusters.size() != color.size() ) {
    std::cerr << "Image::writeImageTiffWithDefinedColors - not correct number"
	      << " of colors or clusters defined!\n";
    exit(-1);
  }

  TIFF *output;
  uint32 width, height;
  char *raster;
  int samplesPerPixel = 3; //R,G,B
  //int samplesPerPixel = 4; //R,G,B,A
  
  // Open the output image
  if((output = TIFFOpen(file, "w")) == NULL){
    std::cerr << "Could not open outgoing image\n";
    exit(42);
  }
  
  // We need to know the width and the height before we can malloc
  width = W();
  height = H();
  int pixelNum = width * height;
  if((raster = (char *) malloc(sizeof(char) * width * height * 
			       samplesPerPixel)) == NULL){
    std::cerr << "Could not allocate enough memory\n";
    exit(42);
  }
  // Converting the picture matrix into the raster...
  int c=0;
  for( int i=0 ; i<H() ; i++ ) 
    for( int j=0 ; j<W() ; j++ ) {
      char tmp = char( pix(i,j) );
      for( int a=0 ; a<3 ; a++ )
	raster[c++] = tmp;//equal colors to each pixels
      if( samplesPerPixel == 4 )
	raster[c++] = 255;//set alpha channel to 255 (not transparent...???)
    }
  //Change given points to colored ones...
  for( int n=0 ; n<clusters.size() ; n++ ) {
    //define a random color
    char red=color[n][0];
    char green=color[n][1];
    char blue=color[n][2];
    for( int i=0 ; i<clusters[n].size() ; i++ ) {
      int pixel = clusters[n][i][0]*W() + clusters[n][i][1]; 
      if( pixel<pixelNum ) {
	int rasterPoint0 = pixel*samplesPerPixel; 
	raster[rasterPoint0]=red;//char(254);
	raster[rasterPoint0+1]=green;//char(0);
	raster[rasterPoint0+2]=blue;//char(0);
      }
    }
  }
  // Write the tiff tags to the file
  TIFFSetField(output, TIFFTAG_IMAGEWIDTH, width);
  TIFFSetField(output, TIFFTAG_IMAGELENGTH, height);
  //TIFFSetField(output, TIFFTAG_COMPRESSION, COMPRESSION_DEFLATE);
  TIFFSetField(output, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
  TIFFSetField(output, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB);
  TIFFSetField(output, TIFFTAG_BITSPERSAMPLE, 8);
  TIFFSetField(output, TIFFTAG_SAMPLESPERPIXEL, samplesPerPixel);

  // Actually write the image
  if(TIFFWriteEncodedStrip(output, 0, raster, 
			   width * height * samplesPerPixel) == 0){
    std::cerr << "Could not write image\n";
    exit(42);
  }
  
  TIFFClose(output);
}

//!Uses colors defined from 0-1 and a palette to color pixels
int Image::writeImageTiffWithDefinedColors( const char *file, 
					    std::vector< std::vector< 
					    std::vector<int> > > &clusters,
					    std::vector<double> &color) {

  if( clusters.size() != color.size() ) {
    std::cerr << "Image::writeImageTiffWithDefinedColors - Compartment and "
	      << "color vectors does not have the same sizes!\n";
    exit(-1);
  }
  TIFF *output;
  uint32 width, height;
  char *raster;
  int samplesPerPixel = 3; //R,G,B
  //int samplesPerPixel = 4; //R,G,B,A
  
  // Open the output image
  if((output = TIFFOpen(file, "w")) == NULL){
    std::cerr << "Could not open outgoing image\n";
    exit(42);
  }
  
  // We need to know the width and the height before we can malloc
  width = W();
  height = H();
  int pixelNum = width * height;
  if((raster = (char *) malloc(sizeof(char) * width * height * 
			       samplesPerPixel)) == NULL){
    std::cerr << "Could not allocate enough memory\n";
    exit(42);
  } 
  // Converting the picture matrix into the raster...
  int c=0;
  for( int i=0 ; i<H() ; i++ ) 
    for( int j=0 ; j<W() ; j++ ) {
      char tmp = char( 254 );
      for( int a=0 ; a<3 ; a++ )
	raster[c++] = tmp;//equal colors to each pixels
      if( samplesPerPixel == 4 )
	raster[c++] = 254;//set alpha channel to 255 (not transparent...???)
    }
  //Change given points to colored ones...
  for( int n=0 ; n<clusters.size() ; n++ ) {
    //define the color from the pallette
    double frac=0.333333333;
    char red,green,blue;
    if( color[n]<frac ) {//black/>blue
      red = green = char(0);
      blue = char(255*color[n]/frac);
    }
    else if( color[n]<2*frac ) {//blue->red
      green=char(0);
      blue = char( 255*(2.0-color[n]/frac) );
      red = char( 255*(color[n]/frac-1.0) );		    
    }
    else {//red->yellow
      green = char( 255*(color[n]/frac-2.0) );
      blue = char(0);
      red = char(254);
    }	    
    for( int i=0 ; i<clusters[n].size() ; i++ ) {
      int pixel = clusters[n][i][0]*W() + clusters[n][i][1]; 
      if( pixel<pixelNum ) {
	int rasterPoint0 = pixel*samplesPerPixel; 
	raster[rasterPoint0]=red;//char(254);
	raster[rasterPoint0+1]=green;//char(0);
	raster[rasterPoint0+2]=blue;//char(0);
      }
    }
  }
  // Write the tiff tags to the file
  TIFFSetField(output, TIFFTAG_IMAGEWIDTH, width);
  TIFFSetField(output, TIFFTAG_IMAGELENGTH, height);
  //TIFFSetField(output, TIFFTAG_COMPRESSION, COMPRESSION_DEFLATE);
  TIFFSetField(output, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
  TIFFSetField(output, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB);
  TIFFSetField(output, TIFFTAG_BITSPERSAMPLE, 8);
  TIFFSetField(output, TIFFTAG_SAMPLESPERPIXEL, samplesPerPixel);

  // Actually write the image
  if(TIFFWriteEncodedStrip(output, 0, raster, 
			   width * height * samplesPerPixel) == 0){
    std::cerr << "Could not write image\n";
    exit(42);
  }
  
  TIFFClose(output);
}


int Image::writeImageTiffWithRedPixels( char *file, 
				      std::vector< std::vector<int> > &redPixels ) {

  TIFF *output;
  uint32 width, height;
  char *raster;
  int samplesPerPixel = 3; //R,G,B
  //int samplesPerPixel = 4; //R,G,B,A

  // Open the output image
  if((output = TIFFOpen(file, "w")) == NULL){
    std::cerr << "Could not open outgoing image\n";
    exit(42);
  }

  // We need to know the width and the height before we can malloc
  width = W();
  height = H();
  int pixelNum = width * height;
  if((raster = (char *) malloc(sizeof(char) * width * height * samplesPerPixel)) == NULL){
    std::cerr << "Could not allocate enough memory\n";
    exit(42);
  }
  // Converting the picture matrix into the raster...
  int c=0;
  for( int i=0 ; i<H() ; i++ ) 
    for( int j=0 ; j<W() ; j++ ) {
      char tmp = char( pix(i,j) );
      for( int a=0 ; a<3 ; a++ )
	raster[c++] = tmp;//equal colors to each pixels
      if( samplesPerPixel == 4 )
	raster[c++] = 255;//set alpha channel to 255 (not transparent...???)
    }
  //Change given points to red ones...
  for( int n=0 ; n<redPixels.size() ; n++ ) {
    int pixel = redPixels[n][0]*W() + redPixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0]=char(254);
      raster[rasterPoint0+1]=raster[rasterPoint0+2]=char(0);
    }
  }
  // Write the tiff tags to the file
  TIFFSetField(output, TIFFTAG_IMAGEWIDTH, width);
  TIFFSetField(output, TIFFTAG_IMAGELENGTH, height);
  //TIFFSetField(output, TIFFTAG_COMPRESSION, COMPRESSION_DEFLATE);
  TIFFSetField(output, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
  TIFFSetField(output, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB);
  TIFFSetField(output, TIFFTAG_BITSPERSAMPLE, 8);
  TIFFSetField(output, TIFFTAG_SAMPLESPERPIXEL, samplesPerPixel);

  // Actually write the image
  if(TIFFWriteEncodedStrip(output, 0, raster, 
			   width * height * samplesPerPixel) == 0){
    std::cerr << "Could not write image\n";
    exit(42);
  }
  
  TIFFClose(output);
}

//!Write the image where circles are filled red
int Image::writeImageTiffWithRedCircles( char *file, 
				  std::vector< std::vector<int> > &centers,
				  std::vector<double> &radii ) {

  if( centers.size() != radii.size() ) {
    std::cerr << "Image::writeImageTiffWithRedCircles"
	 << " Wrong size on centers/radii.\n";
    exit(-1);
  }
  int N=centers.size();
  std::vector< std::vector<int> > c(N); 
  for( int n=0 ; n<N ; n++ ) {
    c[n].resize(2);
    c[n][0]=centers[n][0];
    c[n][1]=centers[n][1];
  }
  std::vector<int> tmp(2);
  for( int n=0 ; n<N ; n++ ) {
    
    for( int x=int(centers[n][0]-radii[n]) ; 
	 x<=int(centers[n][0]+radii[n]) ; x++ )
      for( int y=int(centers[n][1]-radii[n]) ; 
	   y<=int(centers[n][1]+radii[n]) ; y++ ) {
	
	double r=sqrt( double( (x-centers[n][0])*(x-centers[n][0]) +
			       (y-centers[n][1])*(y-centers[n][1]) ) );
	if( x>=0 && x<H() && y>=0 && y<W() && r<=radii[n] ) {
	  tmp[0] = x;tmp[1]=y;
	  //std::cerr << x << " " << y << "\t" << radii[n] << " " << r << " " << n 
	  //   << " " << centers[n][0] << " " << centers[n][1] << "\n";
	  //std::cerr << tmp[0] << " " << tmp[1] << "\n";
	  c.push_back(tmp);
	}
      }
  }
  writeImageTiffWithRedPixels( file,c );
}

//!Writes the image in black and white and adds red and blue pixels
int Image::writeImageTiffWithRedBluePixels( char *file, 
					std::vector< std::vector<int> > &redPixels,
					std::vector< std::vector<int> > &bluePixels ) {

  TIFF *output;
  uint32 width, height;
  char *raster;
  int samplesPerPixel = 3; //R,G,B
  //int samplesPerPixel = 4; //R,G,B,A

  // Open the output image
  if((output = TIFFOpen(file, "w")) == NULL){
    std::cerr << "Could not open outgoing image\n";
    exit(42);
  }

  // We need to know the width and the height before we can malloc
  width = W();
  height = H();
  int pixelNum = width*height;
  if((raster = (char *) malloc(sizeof(char) * width * height * samplesPerPixel)) == NULL){
    std::cerr << "Could not allocate enough memory\n";
    exit(42);
  }
  // Converting the picture matrix into the raster...
  int c=0;
  for( int i=0 ; i<H() ; i++ ) 
    for( int j=0 ; j<W() ; j++ ) {
      char tmp = char( pix(i,j) );
      for( int a=0 ; a<3 ; a++ )
	raster[c++] = tmp;//equal colors to each pixels
      if( samplesPerPixel==4 )
	raster[c++] = 255;//set alpha channel to 255 (not transparent...???)
    }
  //First reset given points
  for( int n=0 ; n<redPixels.size() ; n++ ) {
    int pixel = redPixels[n][0]*W()+redPixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0]=raster[rasterPoint0+1]=raster[rasterPoint0+2]=char(0);
    }
  }
  for( int n=0 ; n<bluePixels.size() ; n++ ) {
    int pixel = bluePixels[n][0]*W()+bluePixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0]=raster[rasterPoint0+1]=raster[rasterPoint0+2]=char(0);
    }
  }
  //Increase the red for the red points
  for( int n=0 ; n<redPixels.size() ; n++ ) {
    int pixel = redPixels[n][0]*W()+redPixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0]=char(254);
    }
  }
  //Increase the Blue for blue points
  for( int n=0 ; n<bluePixels.size() ; n++ ) {
    int pixel = bluePixels[n][0]*W()+bluePixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0+2]=char(254);
    }
  }
  // Write the tiff tags to the file
  TIFFSetField(output, TIFFTAG_IMAGEWIDTH, width);
  TIFFSetField(output, TIFFTAG_IMAGELENGTH, height);
  //TIFFSetField(output, TIFFTAG_COMPRESSION, COMPRESSION_DEFLATE);
  TIFFSetField(output, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
  TIFFSetField(output, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB);
  TIFFSetField(output, TIFFTAG_BITSPERSAMPLE, 8);
  TIFFSetField(output, TIFFTAG_SAMPLESPERPIXEL, samplesPerPixel);

  // Actually write the image
  if(TIFFWriteEncodedStrip(output, 0, raster, width * height * samplesPerPixel) == 0){
    std::cerr << "Could not write image\n";
    exit(42);
  }
  
  TIFFClose(output);
  
}

//!Writes the image in black and white and adds red, green and blue pixels
int Image::writeImageTiffWithRedGreenBluePixels( char *file, 
					  std::vector< std::vector<int> > &redPixels,
					  std::vector< std::vector<int> > &greenPixels,
					  std::vector< std::vector<int> > &bluePixels ) {

  TIFF *output;
  uint32 width, height;
  char *raster;
  int samplesPerPixel = 3; //R,G,B
  //int samplesPerPixel = 4; //R,G,B,A

  // Open the output image
  if((output = TIFFOpen(file, "w")) == NULL){
    std::cerr << "Could not open outgoing image\n";
    exit(42);
  }

  // We need to know the width and the height before we can malloc
  width = W();
  height = H();
  int pixelNum = width*height;
  if((raster = (char *) malloc(sizeof(char) * width * height * samplesPerPixel)) == NULL){
    std::cerr << "Could not allocate enough memory\n";
    exit(42);
  }
  // Converting the picture matrix into the raster...
  int c=0;
  for( int i=0 ; i<H() ; i++ ) 
    for( int j=0 ; j<W() ; j++ ) {
      char tmp = char( pix(i,j) );
      for( int a=0 ; a<3 ; a++ )
	raster[c++] = tmp;//equal colors to each pixels
      if( samplesPerPixel==4 )
	raster[c++] = 255;//set alpha channel to 255 (not transparent...???)
    }
  //First reset given points
  for( int n=0 ; n<redPixels.size() ; n++ ) {
    int pixel = redPixels[n][0]*W()+redPixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0]=raster[rasterPoint0+1]=
	raster[rasterPoint0+2]=char(0);
    }
  }
  for( int n=0 ; n<greenPixels.size() ; n++ ) {
    int pixel = greenPixels[n][0]*W()+greenPixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0]=raster[rasterPoint0+1]=
	raster[rasterPoint0+2]=char(0);
    }
  }
  for( int n=0 ; n<bluePixels.size() ; n++ ) {
    int pixel = bluePixels[n][0]*W()+bluePixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0]=raster[rasterPoint0+1]=
	raster[rasterPoint0+2]=char(0);
    }
  }
  //Increase the red for the red points
  for( int n=0 ; n<redPixels.size() ; n++ ) {
    int pixel = redPixels[n][0]*W()+redPixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0]=char(254);
    }
  }
  //Increase the Green for green points
  for( int n=0 ; n<greenPixels.size() ; n++ ) {
    int pixel = greenPixels[n][0]*W()+greenPixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0+1]=char(254);
    }
  }
  //Increase the Blue for blue points
  for( int n=0 ; n<bluePixels.size() ; n++ ) {
    int pixel = bluePixels[n][0]*W()+bluePixels[n][1]; 
    if( pixel<pixelNum ) {
      int rasterPoint0 = pixel*samplesPerPixel; 
      raster[rasterPoint0+2]=char(254);
    }
  }
  // Write the tiff tags to the file
  TIFFSetField(output, TIFFTAG_IMAGEWIDTH, width);
  TIFFSetField(output, TIFFTAG_IMAGELENGTH, height);
  //TIFFSetField(output, TIFFTAG_COMPRESSION, COMPRESSION_DEFLATE);
  TIFFSetField(output, TIFFTAG_PLANARCONFIG, PLANARCONFIG_CONTIG);
  TIFFSetField(output, TIFFTAG_PHOTOMETRIC, PHOTOMETRIC_RGB);
  TIFFSetField(output, TIFFTAG_BITSPERSAMPLE, 8);
  TIFFSetField(output, TIFFTAG_SAMPLESPERPIXEL, samplesPerPixel);

  // Actually write the image
  if(TIFFWriteEncodedStrip(output, 0, raster, width * height * samplesPerPixel) == 0){
    std::cerr << "Could not write image\n";
    exit(42);
  }
  
  TIFFClose(output);
  
}

//!Function for finding minimas by gradient descent
/*! This function starts from each pixel in the picture and tries to
  find the closest minima using a simple gradient descent walk. It
  then returns the pixels which are minima to more than threshold
  pixels.*/
int Image::findMinimaByGradientDescent(std::vector< std::vector<int> > &ijMin,
				       double valThreshold,
				       int valThresholdFlag,
				       int threshold ) {
  std::vector<int> tmp(3);
  if( ijMin.size() )
    ijMin.resize(0);
  std::vector< std::vector<int> > ijTmp;//To store the values before threshold check
  
  //Find the minimas from each pixel
  for( int iStart=0 ; iStart<H() ; iStart++ )
    for( int jStart=0 ; jStart<W() ; jStart++ ) {
      int i=iStart;
      int j=jStart;
      double value,newValue;
      
      //find the min by walking downhill...
      do {
	newValue=value=pix(i,j);
	int newI=i,newJ=j;
	for(int ii=i-1 ; ii<=i+1 ; ii++ )
	  for(int jj=j-1 ; jj<=j+1 ; jj++ )
	    if( ii>=0 && jj>=0 && ii<H() && jj<W() )
	      if( pix(ii,jj)<newValue ) {
		newValue=pix(ii,jj);
		newI=ii;
		newJ=jj;
	      }
	i=newI;
	j=newJ;	
      } while( newValue<value );
      
      if( !valThresholdFlag || newValue<valThreshold ) {

	//Check if it is a new maximum
	int newMinFlag=1;
	for( int n=0 ; n<ijTmp.size() ; n++ ) {
	  if( i==ijTmp[n][0] && j==ijTmp[n][1] ) {
	    ijTmp[n][2]++;
	    newMinFlag=0;
	    break;
	  }
	}
	//Add if new minimum
	if( newMinFlag ) {
	  int n=ijTmp.size();
	  ijTmp.push_back( tmp );
	  ijTmp[n][0] = i;
	  ijTmp[n][1] = j;
	  ijTmp[n][2] = 1;
	} 
      }
    }

  //Get the mins visited more than threshold times...
  for( int n=0 ; n<ijTmp.size() ; n++ )
    if( ijTmp[n][2]>=threshold )
      ijMin.push_back( ijTmp[n] ); 

  return ijMin.size();
}

//!Function for finding maximas by gradient descent
/*! This function starts from each pixel in the picture and tries to
  find the closest maxima using a simple gradient descent walk. It
  then returns the pixels which are maxima to more then threshold
  pixels. If valThresoldFlag is set only maximas greater than
  valThreshold is returned*/
int Image::findMaximaByGradientDescent(std::vector< std::vector<int> > &ijMax,
				       double valThreshold, 
				       int threshold ) {
  
  std::vector<int> tmp(3);
  if( ijMax.size() )
    ijMax.resize(0);
  std::vector< std::vector<int> > ijTmp;//To store the values before threshold check
  
  std::vector< std::vector<int> > walkTmp;//positions for one walk (start point)
  // Marker for which pixels that have been visited
  std::vector< std::vector<int> > flag( H() );
  for( int i=0 ; i<flag.size() ; i++ )
    flag[i].resize( W() );

  int count=1;
  //Find the maxima from each pixel
  for( int iStart=0 ; iStart<H() ; iStart++ )
    for( int jStart=0 ; jStart<W() ; jStart++ ) {
      int i=iStart;
      int j=jStart;
      double value,newValue;
      walkTmp.resize(1);
      walkTmp[0].resize(2);
      walkTmp[0][0]=i;walkTmp[0][1]=j;
	
      //find the max by walking uphill (greedy)
      if( !flag[i][j] ) {
	do {
	  newValue=value=pix(i,j);
	  int newI=i,newJ=j;
	  //Check all pixels around a given pixel
//  	  for(int ii=i-1 ; ii<=i+1 ; ii++ )
//  	    for(int jj=j-1 ; jj<=j+1 ; jj++ )
//  	      if( ii>=0 && jj>=0 && ii<H() && jj<W() )
//  		if( pix(ii,jj)>newValue ) {
//  		  newValue=pix(ii,jj);
//  		  newI=ii;
//  		  newJ=jj;
//  		}
	  
	  //Check only nearest neighbors
	  for(int a=-1 ; a<=1 ; a+=2 ) {
	    int ii = i+a;
	    if( ii>=0 && ii<H() && pix(ii,j)>newValue ) {
	      newValue=pix(ii,j);
	      newI=ii;
	      newJ=j;
	    }
	    int jj = j+a;  
	    if( jj>=0 && jj<W() && pix(i,jj)>newValue ) {
	      newValue=pix(i,jj);
	      newI=i;
	      newJ=jj;
	    }
	  }
	  i=newI;
	  j=newJ;	
	  std::vector<int> tmpPos(2);
	  tmpPos[0]=i;tmpPos[1]=j;
	  walkTmp.push_back( tmpPos );
	} while( newValue>value && !flag[i][j] );
      }
      //Collect the path data and add one visit for the maximum
      if( !flag[i][j] ) { //new maximum
	tmp[0]=i;tmp[1]=j;tmp[2]=1;
	ijTmp.push_back( tmp );
	int n=count++;//ijTmp.size();
	for( int a=0 ; a<walkTmp.size() ; a++ )
	  flag[ walkTmp[a][0] ][ walkTmp[a][1] ] = n;
      }
      else { //old maximum
	int n = flag[i][j];
	for( int a=0 ; a<walkTmp.size() ; a++ )
	  flag[ walkTmp[a][0] ][ walkTmp[a][1] ] = n;
	ijTmp[n-1][2]++;
      }
    }
  
  //Get the maxima visited more than threshold times and with an intensity
  //value higher than threshold
  for( int n=0 ; n<ijTmp.size() ; n++ )
    if( ijTmp[n][2]>=threshold && pix(ijTmp[n][0],ijTmp[n][1])>valThreshold)
      ijMax.push_back( ijTmp[n] ); 
  
  return ijMax.size();
}

//!Function for finding maximas by gradient descent
/*! This function starts from each pixel in the picture and tries to
  find the closest maxima using a simple gradient descent walk. It
  then returns the pixels which are maxima to more then threshold
  pixels. If valThresoldFlag is set only maximas greater than
  valThreshold is returned. This version saves the basin of attractors
  in boa.*/
int Image::findMaximaByGradientDescent(std::vector< std::vector<int> > &ijMax,
				       std::vector< std::vector< 
				       std::vector<int> > > &boa,
				       double valThreshold, 
				       int threshold ) {
  
  std::vector<int> tmp(3);
  if( ijMax.size() )
    ijMax.resize(0);
  std::vector< std::vector<int> > ijTmp;//To store the values before threshold check
  
  std::vector< std::vector<int> > walkTmp;//positions for one walk (start point)
  // Marker for which pixels that have been visited
  std::vector< std::vector<int> > flag( H() );
  for( int i=0 ; i<flag.size() ; i++ )
    flag[i].resize( W() );

  int count=1;
  //Find the maxima from each pixel
  for( int iStart=0 ; iStart<H() ; iStart++ )
    for( int jStart=0 ; jStart<W() ; jStart++ ) {
      int i=iStart;
      int j=jStart;
      double value,newValue;
      walkTmp.resize(1);
      walkTmp[0].resize(2);
      walkTmp[0][0]=i;walkTmp[0][1]=j;
	
      //find the max by walking uphill (greedy)
      if( !flag[i][j] ) {
	do {
	  newValue=value=pix(i,j);
	  int newI=i,newJ=j;
	  //Check all pixels around a given pixel
//  	  for(int ii=i-1 ; ii<=i+1 ; ii++ )
//  	    for(int jj=j-1 ; jj<=j+1 ; jj++ )
//  	      if( ii>=0 && jj>=0 && ii<H() && jj<W() )
//  		if( pix(ii,jj)>newValue ) {
//  		  newValue=pix(ii,jj);
//  		  newI=ii;
//  		  newJ=jj;
//  		}
	  
	  //Check only nearest neighbors
	  for(int a=-1 ; a<=1 ; a+=2 ) {
	    int ii = i+a;
	    if( ii>=0 && ii<H() && pix(ii,j)>newValue ) {
	      newValue=pix(ii,j);
	      newI=ii;
	      newJ=j;
	    }
	    int jj = j+a;  
	    if( jj>=0 && jj<W() && pix(i,jj)>newValue ) {
	      newValue=pix(i,jj);
	      newI=i;
	      newJ=jj;
	    }
	  }
	  i=newI;
	  j=newJ;	
	  std::vector<int> tmpPos(2);
	  tmpPos[0]=i;tmpPos[1]=j;
	  walkTmp.push_back( tmpPos );
	} while( newValue>value && !flag[i][j] );
      }
      //Collect the path data and add one visit for the maximum
      if( !flag[i][j] ) { //new maximum
	tmp[0]=i;tmp[1]=j;tmp[2]=1;
	ijTmp.push_back( tmp );
	int n=count++;//ijTmp.size();
	for( int a=0 ; a<walkTmp.size() ; a++ )
	  flag[ walkTmp[a][0] ][ walkTmp[a][1] ] = n;
      }
      else { //old maximum
	int n = flag[i][j];
	for( int a=0 ; a<walkTmp.size() ; a++ )
	  flag[ walkTmp[a][0] ][ walkTmp[a][1] ] = n;
	ijTmp[n-1][2]++;
      }
    }
  
  //Get the maxima visited more than threshold times and with an intensity
  //value higher than threshold
  std::vector<int> clusterNum;
  for( int n=0 ; n<ijTmp.size() ; n++ )
    if( ijTmp[n][2]>=threshold && pix(ijTmp[n][0],ijTmp[n][1])>valThreshold) {
      ijMax.push_back( ijTmp[n] ); 
      clusterNum.push_back( n+1 );
    }

  //Save the basins of attraction
  boa.resize( ijMax.size() );
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      for( int n=0 ; n<ijMax.size() ; n++ )
	if( flag[i][j] == clusterNum[n] ) {
	  int tmpSize=boa[n].size();
	  boa[n].resize(tmpSize+1);
	  boa[n][tmpSize].resize(2);
	  boa[n][tmpSize][0]=i;
	  boa[n][tmpSize][1]=j;
	} 
  
  return ijMax.size();
}

//!Function for finding maximas by gradient descent
/*! This function starts from each pixel in the picture and tries to
  find the closest maxima using a simple gradient descent walk. It
  then returns the pixels which are maxima to more then threshold
  pixels. If valThresoldFlag is set only maximas greater than
  valThreshold is returned. This version saves the basin of attractors
  in boa.*/
int Image::findMaximaByGradientDescent(std::vector< std::vector<int> > &ijMax,
				       std::vector< std::vector< 
				       std::vector<int> > > &boa,
				       std::vector< std::vector<int> > &bg,
				       double valThreshold, 
				       int threshold ) {
  
  std::vector<int> tmp(3);
  if( ijMax.size() )
    ijMax.resize(0);
  std::vector< std::vector<int> > ijTmp;//To store the values before threshold check
  
  std::vector< std::vector<int> > walkTmp;//positions for one walk (start point)
  // Marker for which pixels that have been visited
  std::vector< std::vector<int> > flag( H() );
  for( int i=0 ; i<flag.size() ; i++ )
    flag[i].resize( W() );
  
  //Set flag for background pixels to -1
  for( int i=0 ; i<bg.size() ; i++ )
    if( bg[i][0]>=0 && bg[i][0]<H() && bg[i][1]>=0 && bg[i][1]<W() )
      flag[ bg[i][0] ][ bg[i][1] ]=-1;
  
  int count=1;
  //Find the maxima from each pixel
  for( int iStart=0 ; iStart<H() ; iStart++ )
    for( int jStart=0 ; jStart<W() ; jStart++ ) {
      int i=iStart;
      int j=jStart;
      double value,newValue;
      walkTmp.resize(1);
      walkTmp[0].resize(2);
      walkTmp[0][0]=i;walkTmp[0][1]=j;
      
      //find the max by walking uphill (greedy)
      if( !flag[i][j] ) {
	do {
	  newValue=value=pix(i,j);
	  int newI=i,newJ=j;
	  //Check all pixels around a given pixel
	  for(int ii=i-1 ; ii<=i+1 ; ii++ )
	    for(int jj=j-1 ; jj<=j+1 ; jj++ )
	      if( ii>=0 && jj>=0 && ii<H() && jj<W() )
		if( pix(ii,jj)>newValue ) {
		  newValue=pix(ii,jj);
		  newI=ii;
		  newJ=jj;
		}
	  
	  //Check only nearest neighbors
//   	  for(int a=-1 ; a<=1 ; a+=2 ) {
//   	    int ii = i+a;
//   	    if( ii>=0 && ii<H() && pix(ii,j)>newValue ) {
//   	      newValue=pix(ii,j);
//   	      newI=ii;
//   	      newJ=j;
//   	    }
//   	    int jj = j+a;  
//   	    if( jj>=0 && jj<W() && pix(i,jj)>newValue ) {
//   	      newValue=pix(i,jj);
//   	      newI=i;
//   	      newJ=jj;
//   	    }
//	}
	  i=newI;
	  j=newJ;	
	  std::vector<int> tmpPos(2);
	  tmpPos[0]=i;tmpPos[1]=j;
	  walkTmp.push_back( tmpPos );
	} while( newValue>value && !flag[i][j] );
      }
      //Collect the path data and add one visit for the maximum
      if( !flag[i][j] ) { //new maximum
	tmp[0]=i;tmp[1]=j;tmp[2]=1;
	ijTmp.push_back( tmp );
	int n=count++;//ijTmp.size();
	for( int a=0 ; a<walkTmp.size() ; a++ )
	  flag[ walkTmp[a][0] ][ walkTmp[a][1] ] = n;
      }
      else { //old maximum or background
	int n = flag[i][j];
	for( int a=0 ; a<walkTmp.size() ; a++ )
	  flag[ walkTmp[a][0] ][ walkTmp[a][1] ] = n;
	if( flag[i][j]>0 )//old maxima
	  ijTmp[n-1][2]++;
      }
    }
  //Get the maxima visited more than threshold times and with an intensity
  //value higher than threshold
  std::vector<int> clusterNum;
  for( int n=0 ; n<ijTmp.size() ; n++ )
    if( ijTmp[n][2]>=threshold && pix(ijTmp[n][0],ijTmp[n][1])>valThreshold) {
      ijMax.push_back( ijTmp[n] ); 
      clusterNum.push_back( n+1 );
    }
  
  //Save the basins of attraction
  boa.resize( ijMax.size() );
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      for( int n=0 ; n<ijMax.size() ; n++ )
	if( flag[i][j] == clusterNum[n] ) {
	  int tmpSize=boa[n].size();
	  boa[n].resize(tmpSize+1);
	  boa[n][tmpSize].resize(2);
	  boa[n][tmpSize][0]=i;
	  boa[n][tmpSize][1]=j;
	} 
  
  return ijMax.size();
}

//!Uses the mean of neighbouring pixels to smothen the picture
/*!It uses a "boxed" weight of one for all pixels within the radius
  radius except for the central one, which has the weight weight. If
  the "default" radius value is used the eight surrounding pixels are
  used.*/
void Image::smothen(double weight=1.,double radius=1.5) {
  
  if( weight<=0. ) {
    std::cerr << "Image::smothen(d,d) wrong weight (" << weight << ")...\n";
    exit(-1);}
  
  std::vector< std::vector<double> > imgData;
  imgData.resize( H() );
  for(int i=0 ; i<H() ; i++ )
    imgData[i].resize( W() );
  
  //To be sure not to miss any pixels
  int intRad=int(radius+1.);
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ ) {
      double norm=weight-1.;
      imgData[i][j] = norm*pix(i,j);//one more contribution will be added below
      for( int ii=i-intRad ; ii<=i+intRad ; ii++ )
	for( int jj=j-intRad ; jj<=j+intRad ; jj++ ) {
	  double d=sqrt( double( (ii-i)*(ii-i)+(jj-j)*(jj-j) ) );
	  if( d<radius && ii>=0 && ii<H() && jj>=0 && jj<W() ) {
            imgData[i][j] += pix(ii,jj);
	    norm++;
	  }
	}
      if( norm>0. )
	imgData[i][j] /= norm;
      else {
	std::cerr << "Image::smothen() wrong norm weight (" << norm << ")...\n";
	exit(-1);
      }      
    }

  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      setPix(i,j,imgData[i][j]);
}

//!Uses the mean of neighbouring pixels to smothen the picture
/*!It uses a "triangular" weight for all pixels within the radius
  radius, ranging from 1 for the central one and 0 for those at the
  exact distance radius. If the "default" radius value is used the
  eight surrounding pixels are used.*/
void Image::triSmothen(double radius=1.5) {
  
  
  std::vector< std::vector<double> > imgData;
  imgData.resize( H() );
  for(int i=0 ; i<H() ; i++ )
    imgData[i].resize( W() );
  
  //To be sure not to miss any pixels use a integer Radius larger than the real
  int intRad=int(radius+1.);
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ ) {
      double norm=0.;
      for( int ii=i-intRad ; ii<=i+intRad ; ii++ )
	for( int jj=j-intRad ; jj<=j+intRad ; jj++ ) {
	  double d=sqrt( double( (ii-i)*(ii-i)+(jj-j)*(jj-j) ) );
	  if( d<radius && ii>=0 && ii<H() && jj>=0 && jj<W() ) {
	    double weight=1.-d/radius;
	    imgData[i][j] += weight*pix(ii,jj);
	    norm += weight;
	  }
	}
      if( norm>0. )
	imgData[i][j] /= norm;
      else {
	std::cerr << "Image::smothen() wrong norm weight (" << norm << ")...\n";
	exit(-1);
      }      
    }

  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      setPix(i,j,imgData[i][j]);
}

//! Checks if all pixels are between lR-hR and returns 1 if they are (else 0)
int Image::inRange(int lowRange=0,int highRange=255) {
  
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ ) {
      double aux = pix(i,j);
      if( aux<lowRange || aux>highRange )
	return 0; //out of range... 
    }
  return 1; //in range...
}

//! Changes all pixel values so that they are in 0-254, by normalizing
void Image::putInRange(int lowRange=0,int highRange=255) {
  
  int deltaRange=256;

  double min,max;

  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ ) {
      if( (i==0 && j==0) || pix(i,j)<min )
	min = pix(i,j);
      if( (i==0 && j==0) || pix(i,j)>max )
	max = pix(i,j);
    }

  double delta = max-min;

  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ ) {
      double aux = lowRange + int( deltaRange * (pix(i,j)-min)/delta );
      setPix( i,j, aux );
    }
}

//! Inverts all pixels from p to pp=254-p
void Image::invert() {
  
  if( !(inRange()) ) {
    std::cerr << "Image::invert() "
	      << "Warning normalizing before inverting.\n"
	      << min() << " " << max() << "\n";
    putInRange();
  }
  
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      setPix( i,j,255-pix(i,j) );
}

//! Returns the maximum pixel value
double Image::max() {
  
  double maxVal;
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      if( (i==0 && j==0) || pix(i,j)>maxVal )
	maxVal = pix(i,j);
  return maxVal;
}

//! Returns the minimum pixel value
double Image::min() {
  
  double minVal;
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      if( (i==0 && j==0) || pix(i,j)<minVal )
	minVal = pix(i,j);
  return minVal;
}


//!returns the mean pixel values around given pixel with threshold neighorhood 
double Image::neighborhoodMean(int rowPixel,int colPixel,int threshold=1) {

  if( rowPixel<0 || rowPixel>=H() || colPixel<0 || colPixel>=W() ) {
    std::cerr << "Image::neighborhoodMean : pixel out of range, returning 0\n";
    return 0.;
  }

  double mean =0.;
  int meanCount=0;
  for(int row=rowPixel-threshold ; row<=rowPixel+threshold ; row++ )
    for(int col=colPixel-threshold ; col<=colPixel+threshold ; col++ ) 
      if( row>=0 && row<H() && col>=0 && col<W() ) {
	mean += pix(row,col);
	meanCount++;
      }
  return mean/meanCount;
}


//!Adjust the cell positions by using minima in radial derivatives.
void Image::adjustCenters( std::vector< std::vector<int> > &cellCenter, 
			   std::vector< std::vector<int> > &cellCenterAdjusted,
			   double maxRadius=3.) {
  
  int K = 2;
  int N = cellCenter.size();
  std::vector<int> tmp(K);
  cellCenterAdjusted.resize(N,tmp);
  
  for( int n=0 ; n<N ; n++ ) {
    std::vector<int> center(K),low(K),high(K);
    std::vector< std::vector<int> > xyTmp(1,tmp);
    int change=1;
    for( int k=0 ; k<K ; k++ ) {
      xyTmp[0][k] = cellCenter[n][k];
      cellCenterAdjusted[n][k] = cellCenter[n][k];
    }
    while( change ) {
      change=0;
      for( int k=0 ; k<K ; k++ )
	center[k] = low[k] = high[k] = cellCenterAdjusted[n][k]; 
      if( center[0]<0 || center[0]>=H()
	  || center[1]<0 || center[1]>=W() ) {
	std::cerr << "Data point outside image border\n";
	std::cerr << "x -> 0 : " << cellCenter[n][0] << " : " << H() << "\n"; 
	std::cerr << "y -> 0 : " << cellCenter[n][1] << " : " << W() << "\n"; 
	std::cerr << "Using:\n";
	std::cerr << "x -> 0 : " << center[0] << " : " << H() << "\n"; 
	std::cerr << "y -> 0 : " << center[1] << " : " << W() << "\n";	
	exit(-1);
      }
      
      double min,max,diff=0.;
      //Try the x direction
      //Up
      int tmpFlag=0;
      for( int x=center[0] ; x>=center[0]-maxRadius ; x-- ) {
	if( x>=0 && x<(H()-1) ) {
	  diff = pix(x+1,center[1]) - pix(x,center[1]); 
	  if( !tmpFlag || diff>max ) {
	    low[0] = x;
	    max = diff;
	    tmpFlag++;
	  }
	}
      }
      if( !tmpFlag ) { 
	std::cerr << "Did not get any max in x (up)...\n"; 
	std::cerr << tmpFlag << " " << center[0] << " " << center[1] << " "  
	     << maxRadius << "\t" << H() << "\n";
	exit(0); 
      }
      //Down
      diff=0.;
      tmpFlag=0;
      for( int x=center[0] ; x<=center[0]+maxRadius ; x++ ) {
	if( x>=0 && x<(H()-1) ) {
	  diff = pix(x+1,center[1]) - pix(x,center[1]); 
	  if( !tmpFlag || diff<min ) {
	    high[0] = x;
	    min = diff;
	    tmpFlag++;
	  }
	}
      }
      if( !tmpFlag ) { 
	if( center[0]==(H()-1) )
	  high[0] = center[0];
	else {	  
	  std::cerr << "Did not get any min in x (down)...\n"; 
	  std::cerr << tmpFlag << " " << center[0] << " " << center[1] << " "  
	       << maxRadius << "\t" << H() << "\n";
	  exit(0); 
	}
      }
      int newXC = int( 0.5*(1+high[0]+low[0]) );
      //if( newXC>xC ) { xC++;change++; /*std::cerr << n << " moved (x) +1\n";*/}
      //else if( newXC<xC ) { xC--;change++; }
      if( newXC != center[0] ) {
	std::cerr << n << " x: " << center[0] << " -> " << newXC 
	     << "\t<" << high[0] << "," << low[0] << ">\n"; 
	center[0] = newXC;
	change++;
      }
      //Left
      diff=0.;
      tmpFlag=0;
      for( int y=center[1] ; y>=center[1]-maxRadius ; y-- ) {
	if( y>=0 && y<(W()-1) ) {
	  diff = pix(center[0],y+1) - pix(center[0],y); 
	  if( !tmpFlag || diff>max ) {
	    low[1] = y;
	    max = diff;
	    tmpFlag++;
	  }
	}
      }
      if( !tmpFlag ) { 
	std::cerr << "Did not get any max in y (left)...\n"; 
	std::cerr << tmpFlag << " " << center[0] << " " << center[1] << " "  
	     << maxRadius << "\t" << W() << "\n";
	exit(0); 
      }
      //Right
      diff=0.;
      tmpFlag=0;
      for( int y=center[1] ; y<=center[1]+maxRadius ; y++ ) {
	if( y>=0 && y<(W()-1) ) {
	  diff = pix(center[0],y+1) - pix(center[0],y); 
	  if( !tmpFlag || diff<min ) {
	    high[1] = y;
	    min = diff;
	    tmpFlag++;
	  }
	}
      }
      if( !tmpFlag ) { 
	if( center[1]==(W()-1) )
	  high[1] = center[1];
	else {
	  std::cerr << "Did not get any min in y (right)...\n"; 
	  std::cerr << tmpFlag << " " << center[0] << " " << center[1] << " "  
	       << maxRadius << "\t" << W() << "\n";
	  exit(0); 
	}
      }
      int newYC = int( 0.5*(1+high[1]+low[1]) );
      //if( newYC>yC ) { yC++;change++; /*std::cerr << n << " moved (y) +1\n";*/ }
      //else if( newYC<yC ) { yC--;change++; }
      if( newYC != center[1] ) {
	std::cerr << n << " " << center[1] << " y " << newYC << "\n"; 
	center[1] = newYC;
	change++;
      }
      if( change ) {
	for( int k=0 ; k<K ; k++ )
	  cellCenterAdjusted[n][k] = center[k]; 
      }
      //Check if we have been here before to avoid eternal loop
      for( int i=0 ; i<xyTmp.size() ; i++ ) {
	int changeFlag=0;
	for( int k=0 ; k<K ; k++ )
	  if( xyTmp[i][k]==center[k] )
	    changeFlag++;
	if( changeFlag==K )
	  change=0;
      }
      for( int k=0 ; k<K ; k++ )
	tmp[k] = center[k];
      xyTmp.push_back(tmp);
      change=0;
    }
    std::vector<double> delta(K);
    double d=0.;
    for( int k=0 ; k<K ; k++ ) {
      delta[k] = cellCenterAdjusted[n][k]-cellCenter[n][k];
      d += delta[k]*delta[k];
    }
    d = sqrt(d);
    std::cerr << n << " " ;
    for( int k=0 ; k<K ; k++ ) 
      std::cerr << cellCenter[n][k] << " ";
    std::cerr << "\t" ;
    for( int k=0 ; k<K ; k++ ) 
      std::cerr << delta[k] << " "; 
    std::cerr << "\t" << d << "\n";
    
    //Add the border positions
    //tmp[0]=high[0];tmp[1]=center[1];
    //cellCenterAdjusted.push_back(tmp);
    //tmp[0]=low[0];tmp[1]=center[1];
    //cellCenterAdjusted.push_back(tmp);
    //tmp[0]=center[0];tmp[1]=high[1];
    //cellCenterAdjusted.push_back(tmp);
    //tmp[0]=center[0];tmp[1]=low[1];
    //cellCenterAdjusted.push_back(tmp);

  }
}

//!Adjust the cell positions by using minima in radial derivatives.
/*! This version uses four diameter directions ((0,1)(1,0)(1,1)(1,-1))
  and saves the approximated radius for a circle from these points.*/
void Image::adjustCenters4( std::vector< std::vector<int> > &cellCenter, 
			    std::vector< std::vector<int> > &cellCenterAdjusted,
			    std::vector<double> &radius,
			    double maxRadius=3.) {
  
  int K = 2;
  int N = cellCenter.size();
  std::vector<int> tmp(K);
  cellCenterAdjusted.resize(N,tmp);
  radius.resize(N,0.);

  // Define the directions to use when looking for cell borders
  //////////////////////////////////////////////////////////////////////
  int numDir=8;
  std::vector< std::vector<int> > dir(numDir);
  for( int d=0 ; d<numDir ; d++ )
    dir[d].resize(2);
  dir[0][0]=0;dir[0][1]=1;
  dir[1][0]=1;dir[1][1]=1;
  dir[2][0]=1;dir[2][1]=0;
  dir[3][0]=1;dir[3][1]=-1;
  dir[4][0]=0;dir[4][1]=-1;
  dir[5][0]=-1;dir[5][1]=-1;
  dir[6][0]=-1;dir[6][1]=0;
  dir[7][0]=-1;dir[7][1]=1;
  
  // Check if all given cellCenters are inside the image
  //////////////////////////////////////////////////////////////////////
  for( int n=0 ; n<N ; n++ ) {
    if( cellCenter[n][0]<0 || cellCenter[n][0]>=H()
	|| cellCenter[n][1]<0 || cellCenter[n][1]>=W() ) {
      std::cerr << "Data point outside image border\n";
      std::cerr << "x -> 0 : " << cellCenter[n][0] << " : " << H() << "\n"; 
      std::cerr << "y -> 0 : " << cellCenter[n][1] << " : " << W() << "\n"; 
      exit(-1);
    }
  }
  
  // Start moving the center according to the borders in all directions
  //////////////////////////////////////////////////////////////////////
  std::vector< std::vector<int> > xNew(numDir);
  std::vector<int> xStep(K);
  for( int d=0 ; d<numDir ; d++ )
    xNew[d].resize(K);
  for( int n=0 ; n<N ; n++ ) {
    std::vector< std::vector<int> > xyTmp(1,tmp);
    for( int k=0 ; k<K ; k++ )
      cellCenterAdjusted[n][k] = xyTmp[0][k] = cellCenter[n][k];

    int change=1;
    while( change ) {
      change=0;
      
      double min,max,diff=0.;
      //Find cell border in each direction
      for( int d=0 ; d<numDir ; d++ ) {
	double r=0.;
	for( int k=0 ; k<K ; k++ ) {
	  xNew[d][k] = cellCenterAdjusted[n][k];
	  xStep[k] = cellCenterAdjusted[n][k]+dir[d][k]; 
	  r += (xStep[k]-cellCenterAdjusted[n][k])*
	    (xStep[k]-cellCenterAdjusted[n][k]);
	}
	r = sqrt(r);
	int stepCheck=1;
	if( xStep[0]<0 || xStep[0]>=H() || xStep[1]<0 || xStep[1]>=W() 
	    || r>maxRadius ) 
	  stepCheck=0;
	int numStep=0;
	if( stepCheck )
	  min=pix(xStep[0],xStep[1]) -
	    pix(xStep[0]-dir[d][0],xStep[1]-dir[d][1]); 	  
	
	while( stepCheck ) {
	  diff = pix(xStep[0],xStep[1]) - 
	    pix(xStep[0]-dir[d][0],xStep[1]-dir[d][1]); 	  
	  if( diff<min ) {
	    for( int k=0 ; k<K ; k++ )
	      xNew[d][k] = xStep[k];
	    min = diff;
	  }
	  for( int k=0 ; k<K ; k++ ) {
	    xStep[k] += dir[d][k];
	    r += (xStep[k]-cellCenterAdjusted[n][k])*
	      (xStep[k]-cellCenterAdjusted[n][k]);
	  }
	  r = sqrt(r);
	  if( xStep[0]<0 || xStep[0]>=H() || xStep[1]<0 || xStep[1]>=W() 
	      || r>maxRadius ) 
	    stepCheck=0;
	}
      }
      //Calculate a new center from border points.
      for( int k=0 ; k<K ; k++ ) {
	cellCenterAdjusted[n][k] = 0;
	for( int d=0 ; d<numDir ; d++ )
	  cellCenterAdjusted[n][k] += xNew[d][k];
	cellCenterAdjusted[n][k] = int( 0.5 
					+ cellCenterAdjusted[n][k]/numDir );
      }

      //Check if we have been here before to avoid eternal loop
      for( int i=0 ; i<xyTmp.size() ; i++ ) {
	int changeFlag=0;
	for( int k=0 ; k<K ; k++ )
	  if( xyTmp[i][k]==cellCenterAdjusted[n][k] )
	    changeFlag++;
	if( changeFlag==K )
	  change=0;
      }
      for( int k=0 ; k<K ; k++ )
	tmp[k] = cellCenterAdjusted[n][k];
      xyTmp.push_back(tmp);
      change=0;
    }
    //Add border points as well
    //for( int d=0 ; d<numDir ; d++ ) {
    //tmp[0]=xNew[d][0];tmp[1]=xNew[d][1];
    //cellCenterAdjusted.push_back(tmp);

    //Get the smallest radius for an approximation of of the cell as a circle  
    double minR=0;
    for( int d=0 ; d<numDir ; d++ ) {
      double r = 0.;
      for( int k=0 ; k<K ; k++ )
	r += (cellCenterAdjusted[n][k]-xNew[d][k])
	  *(cellCenterAdjusted[n][k]-xNew[d][k]);
      r = sqrt(r);
      if( d==0 || r<minR )
	minR=r;
    }
    radius[n] = minR;
    
    std::vector<double> delta(K);
    double dist=0.;
    for( int k=0 ; k<K ; k++ ) {
      delta[k] = cellCenterAdjusted[n][k]-cellCenter[n][k];
      dist += delta[k]*delta[k];
    }
    dist = sqrt(dist);
    std::cerr << n << " " ;
    for( int k=0 ; k<K ; k++ ) 
      std::cerr << cellCenterAdjusted[n][k] << " (" << cellCenter[n][k] << ") ";
    std::cerr << "\t" ;
    for( int k=0 ; k<K ; k++ ) 
      std::cerr << delta[k] << " "; 
    std::cerr << "\t" << dist << "\n";
  }
}

//! Calculates comparison statistics for pixels within circles
void Image::compareExpressionCircles( Image &I2,
				      std::vector< std::vector<int> > &cellCenter,
				      std::vector<double> &radius, 
				      std::vector< std::vector<double> > &stat ) {
  
  if( H() != I2.H() || W() != I2.W() ) {
    std::cerr << "Image::compareExpressionCircles() Images not in same size.\n";
    return;
  }
  if( cellCenter.size() != radius.size() ) { 
    std::cerr << "Image::compareExpressionCircles() Not same number of cell "
      "positions as radii.\n";
    return;
  }
  int N=cellCenter.size();
  stat.resize( N );
  //Use six different measures for each cell
  int numStat=14;
  for( int n=0 ; n<N ; n++ )
    stat[n].resize(numStat);
  
  //Calculate the statistics
  for( int n=0 ; n<N ; n++ ) {
    for( int i=0 ; i<numStat ; i++ )
      stat[n][i]=0.;
    std::vector<double> i1Vec(0),i2Vec(0);
    for( int x=int(cellCenter[n][0]-radius[n]) ; 
	 x<=int(cellCenter[n][0]+radius[n]) ; x++ )
      for( int y=int(cellCenter[n][1]-radius[n]) ; 
	   y<=int(cellCenter[n][1]+radius[n]) ; y++ ) {
	
	double r=sqrt( double( (x-cellCenter[n][0])*(x-cellCenter[n][0]) +
			       (y-cellCenter[n][1])*(y-cellCenter[n][1]) ) );
	if( x>=0 && x<H() && y>=0 && y<W() && r<=radius[n] ) {
	  //We're inside the circle and image borders...
	  double i1 = pix(x,y);
	  double i2 = I2.pix(x,y);
	  i1Vec.push_back(i1);
	  i2Vec.push_back(i2);
	  stat[n][0] += 1;
	  stat[n][2] += i1;
	  stat[n][3] += i1*i1;
	  stat[n][5] += i2;
	  stat[n][6] += i2*i2;
	  if( i1>0 ) {
	    stat[n][9] += i2/i1;
	    stat[n][11] += (i2-i1)/i1;
	    if( i2>0 ) 
	      stat[n][13] += (i2-i1)/(i1+i2);
	    else 
	      std::cerr << "Image::compareExpressionCircles() : zero "
		   << "intensity in both images, stat["<<n
		   << "][13] not reliable.\n";
	  }
	  else 
	    std::cerr << "Image::compareExpressionCircles() : zero "
		 << "intensity in template, stat["<<n
		 <<"][9,11] not reliable\n";
	  //stat[n][10] //simple mean div
	  //stat[n][12] //simple mean div
	}
      }
    if( stat[n][0] > 0. ) {
      //Median values
      sort(i1Vec.begin(),i1Vec.end());
      sort(i2Vec.begin(),i2Vec.end());
      stat[n][1] = i1Vec[int(0.5 + i1Vec.size()/2)];
      stat[n][4] = i2Vec[int(0.5 + i2Vec.size()/2)];
      stat[n][7] = stat[n][4]/stat[n][1];
      //Mean values
      stat[n][2] /= stat[n][0];
      stat[n][5] /= stat[n][0];
      if( stat[n][0] > 1. ) {
	//Standard deviations
	stat[n][3] /= stat[n][0];
	stat[n][3] -= stat[n][2]*stat[n][2];
	if( stat[n][3]<0. )
	  std::cerr << "error in std calculations for i2 in cell "<<n<<"\n";
	else
	  stat[n][3] = sqrt( stat[n][3] );
	stat[n][6] /= stat[n][0];
	stat[n][6] -= stat[n][5]*stat[n][5];
	if( stat[n][6]<0. )
	  std::cerr << "error in std calculations for i2 in cell "<<n<<"\n";
	else
	  stat[n][6] = sqrt( stat[n][6] );
      }
      else
	stat[n][3] = stat[n][6] = 0.;

      stat[n][8] = stat[n][5]/stat[n][2];    
      stat[n][9] /= stat[n][0];
      stat[n][10] = (stat[n][5]-stat[n][2])/stat[n][2];
      stat[n][11] /= stat[n][0];
      stat[n][12] = (stat[n][5]-stat[n][2])/(stat[n][2]+stat[n][5]);
      stat[n][13] /= stat[n][0];
    }
    else {
      std::cerr << "Image::compareExpressionCircles() : zero "
	   << "pixels in cell, no stat["<<n
	   <<"][x] calculated\n";
    }
  }
}

//!Calculates an average position from a list of pixels eg basin of attraction
/*! The average is weighted using intensity values in the pixels*/
void Image::cellCenterFromPixels( std::vector< std::vector< std::vector<int> > > &pixels,
				  std::vector< std::vector<int> > &cellCenter) {
  
  if( pixels.size() != cellCenter.size() ) {
    std::cerr << "cellCenterFromPixels Warning changing size on cellCenter\n";
    cellCenter.resize( pixels.size() );
  }
  for( int i=0 ; i<pixels.size() ; i++ )
    if( cellCenter[i].size() < 2 )
      cellCenter[i].resize(2);
  
  for( int i=0 ; i<pixels.size() ; i++ ) {
    double  mean0=0.0,mean1=0.0,weightNorm=0.0;

    for( int j=0 ; j<pixels[i].size() ; j++ ) {
      if( pixels[i][j].size() != 2 ) {
	std::cerr << "cellCenterFromPixels wrong size of pixels positions\n";
	exit(-1);
      }
      double weight = pix(pixels[i][j][0],pixels[i][j][1]);
      weightNorm += weight;
      mean0 += weight*pixels[i][j][0];
      mean1 += weight*pixels[i][j][1];
    }
    mean0 /= weightNorm; //*pixels[i].size();
    mean1 /= weightNorm; //*pixels[i].size();
    cellCenter[i][0] = int( mean0 + 0.5 );
    cellCenter[i][1] = int( mean1 + 0.5 );
  }
}


//!Calculates "polarization" wrt cellCenter of given pixels
void Image::polarization( Image &gfpI, std::vector< std::vector< std::vector<int> > > &pixels,
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
      double norm = sqrt( r0*r0+r1*r1 );
      if( norm>0. ) {
	r0 /= norm;
	r1 /= norm;
      }
      double weight=0.;
      double ref=pix(pixels[i][j][0],pixels[i][j][1]);
      double gfp=gfpI.pix(pixels[i][j][0],pixels[i][j][1]);
      if( ref != 0. )
	weight = gfp/ref;
      else 
	std::cerr << "Image::polarization No ref signal in pixel.\n"
	     << "Using zero weight instead of " << gfp << "/" << ref << "\n";
      
      p0 += weight*r0;
      p1 += weight*r1;
    }
    polarization[i][0] = p0;
    polarization[i][1] = p1;
    polarization[i][2] = sqrt( p0*p0+p1*p1 );
  }
}

//!compare intensities in two different images around a line
void Image::comparePolarizationPixels( Image &I2,std::vector< std::vector<int> > &p,
				       std::vector< std::vector<int> > &pixels,
				       std::vector<double> &statTmp ) {
  if( H() != I2.H() || W() != I2.W() ) {
    std::cerr << "Image::comparePolarizationPixels() Images not in same size.\n";
    return;
  }
  if( p.size() !=2 ) {
    std::cerr << "Image::comparePolarizationPixels() "
	 << "Rectangle must be defined from two points.\n";
    exit(0);
  }
  //Make sure that p[0][0]<=p[1][0] (used by calculations below)
  if( p[1][0]<p[0][0] ) {
    std::cerr << "image::comparePolarizationPixels() "
	 << "Switching points sorted for [0]...\n";
    int tmp0=p[0][0],tmp1=p[0][1];
    p[0][0]=p[1][0];p[0][1]=p[1][1];
    p[1][0]=tmp0;p[1][1]=tmp1;
  }

  //Get the orthogonal std::vector to the line defined by p
  double d0,d1,k0,k1,Ok0,Ok1,norm;
  d0=p[1][0]-p[0][0];
  d1=p[1][1]-p[0][1];
  norm=1./(sqrt(d0*d0+d1*d1));
  k0=norm*d0;
  k1=norm*d1;
  Ok0=-k1;
  Ok1=k0;

  if( k0==0 || k1==0 ) {
    std::cerr << "Image::comparePolarizationPixels() WARNING k=(" 
	 << k0 << "," << k1 << ")\n";
  }
  double meanA=0.,meanB=0.,mean2A=0.,mean2B=0.;
  double meanDivA=0.,meanDivB=0.;
  int numA=0,numB=0;
  std::vector< std::vector<int> > tmpPix;
  for( int i=0 ; i<pixels.size() ; i++ ) {
    
    if( pixels[i][1] > int( p[0][1] + k1 * (pixels[i][0]-p[0][0])/k0 ) ) {
      meanA += pix(pixels[i][0],pixels[i][1]);
      mean2A += I2.pix(pixels[i][0],pixels[i][1]);
      meanDivA += I2.pix(pixels[i][0],pixels[i][1])/
	pix(pixels[i][0],pixels[i][1]);
      tmpPix.push_back( pixels[i] );
      numA++;
    }
    else {
      meanB += pix(pixels[i][0],pixels[i][1]);
      mean2B += I2.pix(pixels[i][0],pixels[i][1]);
      meanDivB += I2.pix(pixels[i][0],pixels[i][1])/
	pix(pixels[i][0],pixels[i][1]);      
      numB++;
    }
  }
  if( numA==0 || numB==0 ) {
    std::cerr << "Image::comparePolarizationPixels() No statistics "
	 << "returning zero\n";
    std::cerr << numA << " " << numB << "\n"; 
    statTmp.resize(3);
    statTmp[0] = 0.;
    statTmp[1] = 0.;
    statTmp[2] = 0.00001;    
    return;
  }
  meanA /= numA;
  mean2A /= numA;
  meanDivA /= numA,
  meanB /= numB;
  mean2B /= numB;
  meanDivB /= numB;

  //double S= ( (mean2A-mean2B)/(mean2A+mean2B) ) / 
  //( (meanA-meanB)/(meanA+meanB) );
  //double S= (meanDivA-meanDivB)/(meanDivA+meanDivB);
  
  double S= (mean2A/meanA - mean2B/meanB);// /(mean2A/meanA+mean2B/meanB); 
  //double S2= (mean2A - mean2B) / (mean2A + mean2B); 
  //double S3= (mean2A/meanA - mean2B/meanB)/(mean2A/meanA+mean2B/meanB); 
  statTmp.resize(3);
  statTmp[0] = S*Ok0;
  statTmp[1] = S*Ok1;
  statTmp[2] = S;
  pixels = tmpPix;

//   std::cerr << numA+numB << " " << numA << " " << numB << "\t| " 
//        << (meanA*numA+meanB*numB)/(numA+numB) << " " 
//        << meanA << " " << meanB << "\t| "
//        << (mean2A*numA+mean2B*numB)/(numA+numB) << " " 
//        << mean2A << " " << mean2B << "\t| "
//        << (mean2A*numA+mean2B*numB) / (meanA*numA+meanB*numB) << " " 
//        << mean2A/meanA << " " << mean2B/meanB << "\t| "
//        << (meanDivA*numA+meanDivB*numB)/(numA+numB) << " " 
//        << meanDivA << " " << meanDivB << "\n";
  
}



int Image::findEdges(std::vector< std::vector<int> > &ijEdges,double threshold=10.,
		      int neighborhood=1) {

  std::vector<int> tmp(2);
  if( ijEdges.size() )
    ijEdges.resize(0);

  for( int i=neighborhood ; i<H()-neighborhood ; i++ )
    for( int j=neighborhood ; j<W()-neighborhood ; j++ ) {
      
      double xDeriv=0.,yDeriv=0.;;
      for( int n=0 ; n<neighborhood ; n++ ) {
	xDeriv += pix(i,j+n+1) - pix(i,j-n);
	yDeriv += pix(i+n+1,j) - pix(i-n,j);
      }
      xDeriv = fabs(xDeriv);// /pix(i,j);
      yDeriv = fabs(yDeriv);// /pix(i,j);
      double deriv = xDeriv+yDeriv;
      if( deriv>threshold ) {
	int n=ijEdges.size();
	ijEdges.push_back( tmp );
	ijEdges[n][0] = i;
	ijEdges[n][1] = j;
      }
    }
  
  return ijEdges.size();
}

void Image::firstDer(int neighborhood=1) {
  
  std::vector< std::vector<double> > tmp( H() );
  for( int i=0 ; i<tmp.size() ; i++ )
    tmp[i].resize( W() );

  for( int i=neighborhood ; i<H()-neighborhood ; i++ )
    for( int j=neighborhood ; j<W()-neighborhood ; j++ ) {
      
      double xDeriv=0.,yDeriv=0.;
      for( int n=0 ; n<neighborhood ; n++ ) {
	xDeriv += pix(i,j+n+1) - pix(i,j-n);
	yDeriv += pix(i+n+1,j) - pix(i-n,j);
      }
      xDeriv = fabs(xDeriv); // /pix(i,j);
      yDeriv = fabs(yDeriv); // /pix(i,j);
      double deriv = xDeriv+yDeriv;
      tmp[i][j] = deriv;
    }
  for( int i=neighborhood ; i<H()-neighborhood ; i++ )
    for( int j=neighborhood ; j<W()-neighborhood ; j++ )
      setPix(i,j,tmp[i][j]);
}

int Image::find2ndDer(std::vector< std::vector<int> > &ijPixels,double threshold=0.,
		      int neighborhood=1) {

  std::vector<int> tmp(2);
  if( ijPixels.size() )
    ijPixels.resize(0);

  for( int i=neighborhood ; i<H()-neighborhood ; i++ )
    for( int j=neighborhood ; j<W()-neighborhood ; j++ ) {
      
      double xy2Deriv=0.;
      int count=0;
      for( int n=1 ; n<=neighborhood ; n++ ) {
	xy2Deriv += pix(i,j+n) + pix(i,j-n) + pix(i+n,j) + pix(i-n,j);
	count += 4;
      }
      xy2Deriv -= count*pix(i,j);

      xy2Deriv /= pix(i,j);

      if( xy2Deriv>threshold ) {
	int n=ijPixels.size();
	ijPixels.push_back( tmp );
	ijPixels[n][0] = i;
	ijPixels[n][1] = j;
      }
    }
  
  return ijPixels.size();
}

//!Calculates the 2nd derivative at each pixel
/*! The neighborhood flag is how many pixels from the original one that is used to calculate the derivative. The normFlag is used if one wants to norm with the pixel value.*/
void Image::scndDer( int neighborhood=1 , int normFlag=0 ) {

  std::vector< std::vector<double> > tmp( H() );
  for( int i=0 ; i<tmp.size() ; i++ )
    tmp[i].resize( W() );

  // Calclulate the 2nd derivatives 
  for( int i=neighborhood ; i<H()-neighborhood ; i++ )
    for( int j=neighborhood ; j<W()-neighborhood ; j++ ) {
      double xy2Deriv=0.;
      int count=0;
      for( int n=1 ; n<=neighborhood ; n++ ) {
	xy2Deriv += pix(i,j+n) + pix(i,j-n) + pix(i+n,j) + pix(i-n,j);
	count += 4;
      }
      xy2Deriv -= count*pix(i,j);
      
      if( normFlag ) 
	xy2Deriv /= pix(i,j); //If norm to pixel value wanted...
      
      tmp[i][j] = xy2Deriv;
    }
  
  //Copy them into the pixels
  for( int i=0 ; i<H() ; i++ )
    for( int j=0 ; j<W() ; j++ )
      setPix( i,j,tmp[i][j] );
  putInRange();

}


//!Finds pixels within a rectangle around given points p
void Image::findPixelNeighborsRectangle(double r,std::vector< std::vector<int> > &p,
					std::vector< std::vector<int> > &pixels) {

  if( p.size() !=2 ) {
    std::cerr << "image::findPixelNeighborsRectangle "
	 << "Rectangle must be defined from two points.\n";
    exit(0);
  }
  //Make sure that p[0][0]<=p[1][0] (used by calculations below)
  if( p[1][0]<p[0][0] ) {
    std::cerr << "image::findPixelNeighborsRectangle "
	 << "Switching points sorted for [0]...\n";
    int tmp0=p[0][0],tmp1=p[0][1];
    p[0][0]=p[1][0];p[0][1]=p[1][1];
    p[1][0]=tmp0;p[1][1]=tmp1;
  }

  //Get the orthogonal std::vector to the line defined by p
  double d0,d1,k0,k1,Ok0,Ok1,norm;
  d0=p[1][0]-p[0][0];
  d1=p[1][1]-p[0][1];
  norm=1./(sqrt(d0*d0+d1*d1));
  k0=norm*d0;
  k1=norm*d1;
  Ok0=-k1;
  Ok1=k0;
  //Define the corners of the rectangle
  std::vector< std::vector<double> > corner(4);
  for( int a=0 ; a<corner.size() ; a++ )
    corner[a].resize(2);

    //upper left
    corner[0][0] = p[0][0] + r*Ok0;
    corner[0][1] = p[0][1] + r*Ok1;
    //lower left
    corner[1][0] = p[0][0] - r*Ok0;
    corner[1][1] = p[0][1] - r*Ok1;
    //upper right
    corner[2][0] = p[1][0] + r*Ok0;
    corner[2][1] = p[1][1] + r*Ok1;
    //lower right
    corner[3][0] = p[1][0] - r*Ok0;
    corner[3][1] = p[1][1] - r*Ok1;

  int min0,min1,max0,max1;
  for( int a=0 ; a<corner.size() ; a++ ) {
    if( a==0 || corner[a][0]<min0 )
      min0=int(corner[a][0]);
    if( a==0 || corner[a][1]<min1 )
      min1=int(corner[a][1]);
    if( a==0 || corner[a][0]>max0 )
      max0=int(corner[a][0]+1);
    if( a==0 || corner[a][1]>max1 )
      max1=int(corner[a][1]+1);
  }
  //Check the image boundaries
  if(min0<0) min0=0;
  if(min1<0) min0=0;
  if(max0>H()) max0=H();
  if(max1>W()) max1=W();
  //Find pixels within all lines
  //Go through potential pixels... 
  std::vector<int> tmpPix(2);
  for( int i0=min0 ; i0<max0 ; i0++ ) 
    for( int i1=min1 ; i1<max1 ; i1++ ) {
      //Do I really need two???
      if( d1>=0 && i1 < int( corner[0][1] + k1 * (i0-corner[0][0])/k0 ) &&
	  i1 > int( corner[1][1] + k1 * (i0-corner[1][0])/k0 ) &&
	  i1 > int( corner[1][1] + Ok1 * (i0-corner[1][0])/Ok0 ) &&
	  i1 < int( corner[3][1] + Ok1 * (i0-corner[3][0])/Ok0 ) ) {
	tmpPix[0]=i0;tmpPix[1]=i1;
	pixels.push_back(tmpPix);
      }
      else if( d1<0. && i1 < int( corner[0][1] + k1 * (i0-corner[0][0])/k0 ) &&
	       i1 > int( corner[1][1] + k1 * (i0-corner[1][0])/k0 ) &&
	       i1 < int( corner[1][1] + Ok1 * (i0-corner[1][0])/Ok0 ) &&
	       i1 > int( corner[3][1] + Ok1 * (i0-corner[3][0])/Ok0 ) ) {
	tmpPix[0]=i0;tmpPix[1]=i1;
	pixels.push_back(tmpPix);
      }
    }
}

void Image::zeroPixels( std::vector< std::vector<int> > &zero ) {
  
  for( int i=0 ; i<zero.size() ; i++ )
    if( zero[i][0]>=0 && zero[i][0]<H() && zero[i][1]>=0 && zero[i][1]<W() )
      setPix(zero[i][0],zero[i][1],0.);
}
