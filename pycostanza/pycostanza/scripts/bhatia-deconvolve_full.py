#!/usr/bin/env python2
# -*- coding: utf-8 -*-
import numpy as np
import tifffile as tiff
from scipy.ndimage.filters import gaussian_filter, median_filter
import os
from stackAlign.deconv import create_psf, deconvolve
import gc

import phenotastic.file_processing as fp
from phenotastic.misc import autocrop, listdir, mkdir

fname = '/home/henrik/data/from-marcus/R2D2 4DAS segmentation and quantification trial_for sending to Henrik_20 august 2018.lif - 5.3 4DAS-manually_cropped.tif'
f = fp.tiffload(fname)
f.data = autocrop(f.data, fct=np.max, threshold=100)
f.data = f.data.astype(np.float64)
res = fp.get_resolution(f)
data = f.data
del f
gc.collect()

MAGNIFICATION = 25
NA = 0.95

iterations = [7, 8, 8]
nchannels = len(iterations)

iopair = (fname, fname[:-4] + '_deconv.tif')

fin = iopair[0]
fout = iopair[1]
print('Now deconvolving ' + str(iopair[0]))

zshape = data.shape[0] // 2. + 2.
rshape =  data.shape[-1] // 2. + 2.

zdims = zshape * res[0]
rdims = rshape * res[1]


ex_wavelens = [488, 514, 561]
em_wavelens = [np.mean([492, 516]), np.mean([519, 535]), np.mean([566, 590])]
pinhole_radii = [102.184968450395 / 2. / MAGNIFICATION]*3

data = data.astype(np.float32)
assert(len(iterations) == len(em_wavelens) == len(ex_wavelens) == data.shape[1])

for ii in xrange(nchannels):
    gc.collect()
    cpsf = create_psf(zshape, rshape, zdims, rdims, ex_wavelen=ex_wavelens[ii],
                      em_wavelen=em_wavelens[ii], pinhole_radius=pinhole_radii[ii],
                      num_aperture=NA, magnification=MAGNIFICATION, pinhole_shape='round')
    data[:, ii] = deconvolve(data[:, ii], psf_vol=cpsf, iterations=iterations[ii], threshold=150)

f.data = data.copy()
f.data[f.data > np.iinfo(np.uint16).max] = np.iinfo(np.uint16).max
f.data = f.data.astype(np.uint16)
#    tiff.imshow(f.data[:,0], vmax=2000)

fp.tiffsave(fout, data=f.data, metadata={'spacing':fp.get_resolution(f)[0]}, resolution=fp.get_resolution(f))

#a = fp.tiffload('1_deconv_test.tif')


