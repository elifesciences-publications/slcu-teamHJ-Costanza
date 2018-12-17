#!/usr/bin/env python2
# -*- coding: utf-8 -*-
import numpy as np
import tifffile as tiff
from pycostanza.steepest import get_footprint, steepest_ascent
from pycostanza.labels import erode, dilate, merge_labels_distance, remove_labels_intensity
from pycostanza.labels import remove_labels_size, relabel, merge_labels_depth
from scipy.ndimage.filters import gaussian_filter, median_filter
import mahotas as mh

import phenotastic.file_processing as fp
from phenotastic.misc import autocrop

### Import example inage
fname = '/home/henrik/pWUS-3XVENUS-pCLV3-mCherry-on_npa-6-72h_deconv.tif'
f = fp.tiffload('/home/henrik/pWUS-3XVENUS-pCLV3-mCherry-on_npa-6-72h_deconv.tif')
f.data[:, 2] = 0
f.data = autocrop(f.data, fct=np.max, threshold=100)
int_img = f.data[:, 0]
del f

### Preprocessing
smooth_img = int_img.copy()
int_img[int_img < mh.otsu(int_img, True) / 2.] = 0

smooth_img = median_filter(smooth_img, footprint=get_footprint(3, 3))
smooth_img = median_filter(smooth_img, footprint=get_footprint(3, 3))

smooth_img = gaussian_filter(smooth_img, sigma=[1, 2, 2])
smooth_img = gaussian_filter(smooth_img, sigma=[1, 2, 2])
smooth_img = gaussian_filter(smooth_img, sigma=[1, 2, 2])

### Perform initial segmentation
mask = int_img > 0
lab_img = steepest_ascent(smooth_img , (.19, .23, .23), connectivity=3, mask=mask)

### Merge and remove labels
lab_img = merge_labels_distance(lab_img, smooth_img, threshold=5., resolution=(.19, .23, .23))
lab_img = remove_labels_size(lab_img, min_size=30, max_size=None, remove_bordering=False)
lab_img = remove_labels_intensity(lab_img, int_img, threshold=200.)
lab_img = merge_labels_depth(lab_img, int_img, threshold=200., connectivity=3)
lab_img = relabel(lab_img)

### Try to close holes and such
lab_img = erode(lab_img)
lab_img = dilate(lab_img)
lab_img = erode(lab_img)
lab_img = dilate(lab_img)

### Make sizes a bit more realistic, and remove scrap
lab_img = erode(lab_img)
lab_img = erode(lab_img)
lab_img = remove_labels_size(lab_img, min_size=20, max_size=None, remove_bordering=False)
lab_img = relabel(lab_img)

### Look at that beauty for a second
tiff.imshow(lab_img)
#fp.tiffsave('labeled_test.tif', lab_img.astype(np.uint16), resolution=(.19,.23,.23))


