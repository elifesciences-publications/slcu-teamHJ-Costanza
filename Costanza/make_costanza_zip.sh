#!/bin/bash

WDIR="tmpdir"
ZIPNAME="Costanza"

ant clean
ant
mkdir $WDIR
cd $WDIR
cp -r ../CostanzaIJPlugin/dist $ZIPNAME
zip -r $ZIPNAME $ZIPNAME
mv $ZIPNAME.zip ../
rm -rf $ZIPNAME
cd ..
rm -rf $WDIR
