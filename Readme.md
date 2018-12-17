# COSTANZA, [CO]nfocal [ST]ack [AN]aly[Z]er [A]pplication

Costanza is an [ImageJ](https://imagej.nih.gov/ij/) plugin that can be used to 
segment cells, either by marking voxels belonging to individual cells in membrane 
marked data (mainly applied to 2D images) or by identifying cell centers in nuceli 
data (both in 2D images and 3D stacks). There is also a terminal-based java implementation for 
running batch jobs found in [Costanza/CostanzaTextUI](https://gitlab.com/slcu/teamhj/Costanza/Costanza/CostanzaTextUI).

<b>pycostanza</b> is a python implementation of the Costanza segmentation algorithm
with extended features authored by henrik.aahl@slcu.cam.ac.uk. It is found in 
[pycostanza](https://gitlab.com/slcu/teamhj/Costanza/pyconstanza).

### Authors:

Henrik Åhl, Michael Green, Pawel Krupinski, Pontus Melke, Patrik Sahlin, Henrik Jönsson, 
[Computational Biology & Biological Physics](http://cbbp.thep.lu.se), Lund University &
[Sainsbury Laboratory](http://www.slcu.cam.ac.uk), University of Cambridge.

Main contact: henrik.jonsson@slcu.cam.ac.uk

### Source:

Available via (this) git repository.

```
git clone git@gitlab.com:slcu/teamHJ/Costanza.git
```

### Compilation and Installation:

Compile and create the Costanza.zip file from the source code provided in this repository.

```
cd Costanza/Costanza/
ant compile
./make_costanza_zip.sh
```

Alternatively, download [Costanza.zip](http://www.thep.lu.se/~henrik/Costanza/download/Costanza.zip).

Extract the Costanza.zip file into the Imagej plugins folder, or subfolder, restart 
ImageJ, and there will be a new "Costanza" directory in the Plugins menu or submenu.

### Requires:

ImageJ 1.38 or newer.

### Description:

Costanza is used to segment compartments (cells) in a stack and to extract 
quantitative data for the extracted compartments, including intensities from a second stack.

### Usage:

Open the Costanza plugin and the image/stack you want to segment. 
Choose parameters for the including processes, select input and output options 
and run the plugin. 

More information available in the user guide in the Costanza/Costanza/doc folder,
also available [here](http://www.thep.lu.se/~henrik/Costanza/doc/userguide.pdf).
