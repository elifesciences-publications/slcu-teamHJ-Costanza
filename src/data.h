using namespace std;
#include<vector>

int readData( char *file,vector< vector<double> > & data );
int readDataXYZ( char *file,vector< vector<double> > & data );
int readDataXYR( char *file,vector< vector<double> > & centers, 
		 vector<double> &radii );
int readDataXY( char *file,vector< vector<double> > & data );
void convertData( vector< vector<double> > &inData,
		  vector< vector<int> > &outData, double zScale=1.,
		  double xyScale=1. );
void convertData( vector< vector<int> > &inData,
		  vector< vector<double> > &outData, double zScale=1.,
		  double xyScale=1. );

void convertDataToImage( vector< vector<double> > &inData,
			 vector< vector<int> > &outData,
			 double xyScale=1. );
void convertDataToImageXY( vector< vector<double> > &inData,
			 vector< vector<int> > &outData,
			 double xyScale=1. );

int readPara( char *file,vector<double> & para );
void affineTransformation( vector< vector<double> > &inData,
			   vector< vector<double> > &outData,
			   vector<double> &para ); 
void affineToMatrix( vector<double> &para,vector<double> &A );
int readMatrix( char *file,vector<double> & matrix );
void transformation( vector< vector<double> > &inData,
			   vector< vector<double> > &outData,
			   vector<double> &A ); 
