#!/usr/bin/env python2
# -*- coding: utf-8 -*-
import numpy as np
import tifffile as tiff
from pycostanza.steepest import get_footprint, steepest_ascent
from pycostanza.labels import erode, dilate, merge_labels_distance, remove_labels_intensity
from pycostanza.labels import remove_labels_size, relabel, merge_labels_depth, merge_labels_small2closest
from scipy.ndimage.filters import gaussian_filter, median_filter
import mahotas as mh
import os

import phenotastic.file_processing as fp
from phenotastic.misc import autocrop, listdir, mkdir

### Import example inage
dir_ = '/home/henrik/data/from-marcus/to_segment/'
dir_ = '/home/henrik/data/from-marcus/ad_ab_deconvolved/'
dir_ = '/home/henrik/bhatia_transfer/'

files = listdir(dir_)
files.sort()

#fname = '/home/henrik/data/from-marcus/Ad ab volumes/LEFT AB R2D2 4DAS_6 SEP 2018 too young.lif - 1_4das.tif'
outdir = '/home/henrik/out_bhatia'
mkdir(outdir)

#for fname in files:
##fname = files[0]
#    f = fp.tiffload(fname)
##    for ii in xrange(3):
#    print f.data[:,0].sum(), f.data[:,1].sum(), f.data[:,2].sum()
#tiff.imshow(f.data)
from skimage.exposure import rescale_intensity
from skimage.morphology import remove_small_objects

for fname in files:
    f = fp.tiffload(fname)
    #f.data[:, 2] = 0
    f.data = autocrop(f.data, fct=np.max, threshold=100)
    f.data = f.data.astype(np.float64)
    resolution = fp.get_resolution(f)

    int_img = f.data.copy()
    int_img[:, 1] /= np.max(int_img[:, 1])
    int_img[:, 2] /= np.max(int_img [:, 2])
    int_img = int_img [:, 1:]
    int_img = np.max(int_img, axis=1)
    int_img *= np.iinfo(np.uint16).max
    int_img = int_img.astype(np.uint16)

    #del f

    from skimage.morphology import binary_closing, binary_opening
    ### Preprocessing
    footprint = get_footprint(3, 2)
    mask = int_img < 1. * mh.otsu(int_img, True)
    from skimage.morphology import remove_small_objects
    mask = remove_small_objects(mask, min_size=200, connectivity=2)
    #mask = binary_closing(mask, footprint)
    #mask = binary_opening(mask, footprint)
    int_img[mask] = 0

    smooth_img = int_img.copy()
    smooth_img = median_filter(smooth_img, size=1, footprint=get_footprint(3, 2))
    #    smooth_img = median_filter(smooth_img, size=1, footprint=get_footprint(3, 2))

    smooth_img = gaussian_filter(smooth_img, sigma=[.5, 1, 1])
    smooth_img = gaussian_filter(smooth_img, sigma=[.5, 1, 1])
    smooth_img = gaussian_filter(smooth_img, sigma=[.5*2/3, 1*2/3, 1*2/3])
    smooth_img = gaussian_filter(smooth_img, sigma=[.5*1/3, 1*1/3, 1*1/3])
    #smooth_img = gaussian_filter(smooth_img, sigma=[.5*1/4, 1*1/4, 1*1/4])

    #smooth_img = gaussian_filter(smooth_img, sigma=[.5, 1, 1])

    ### Perform initial segmentation
    lab_img = steepest_ascent(smooth_img, resolution, connectivity=2, mask=np.logical_not(mask))

    ### Merge and remove labels
    lab_img = merge_labels_distance(lab_img, smooth_img, threshold=1.5, resolution=resolution)
    lab_img = merge_labels_small2closest(lab_img, threshold=25, distance_upper_bound=3., resolution=None)
    lab_img = remove_labels_size(lab_img, min_size=200, max_size=None, resolution=None)
    #        lab_img = merge_labels_depth(lab_img, int_img, threshold=2000., connectivity=2)
    lab_img = remove_labels_intensity(lab_img, int_img, threshold=3000.)
    lab_img = remove_labels_intensity(lab_img, f.data[:,1], threshold=20.)

    lab_img = relabel(lab_img)


    ''' DATA COLLECTION '''
    ### Look at that beauty for a second
    from skimage.measure import regionprops

    ### Normalise to mDII
    diimax = np.max(mh.labeled.labeled_max(f.data[:, 1], lab_img))
    mdiimax = np.max(mh.labeled.labeled_max(f.data[:, 2], lab_img))

    f.data[:, 1] *= diimax / mdiimax / mdiimax
    f.data[:, 2] /= mdiimax

    # Get expression / ratios
    rp1 = regionprops(lab_img, f.data[:, 1])
    rp2 = regionprops(lab_img, f.data[:, 2])
    ratios = [rp1[ii].mean_intensity / rp2[ii].mean_intensity for ii in xrange(len(rp1))]

    sample, leaf, side = os.path.basename(fname).split(' ')[:3]
    oname = os.path.join(outdir, os.path.basename(fname))[:-4] + '_cells.dat'
    with open(os.path.join(outdir, oname), 'w') as f:
        f.write('{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n'.format(
                'sample', 'leaf', 'side', 'label',
                'DII_mean',  'DII_min',  'DII_max',
                'mDII_mean', 'mDII_min', 'mDII_max',
                'mean_ratio', 'min_ratio', 'max_ratio',
                'size',# 'minor_axis_length', 'major_axis_length',
                'solidity'))
        for ii in xrange(len(rp1)):
            f.write('{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n'.format(
                    sample, leaf, side, rp1[ii].label,
                    rp1[ii].mean_intensity, rp1[ii].min_intensity, rp1[ii].max_intensity,
                    rp2[ii].mean_intensity, rp2[ii].min_intensity, rp2[ii].max_intensity,
                    rp1[ii].mean_intensity / rp2[ii].mean_intensity, rp1[ii].min_intensity / (rp2[ii].min_intensity + 1e-15), rp1[ii].max_intensity / rp2[ii].max_intensity,
                    len(rp1[ii].coords)*np.product(resolution),# rp1[ii].minor_axis_length, rp1[ii].major_axis_length,
                    rp1[ii].solidity))


    # Plot centroids
    ratio_plot = np.zeros(lab_img.shape)
    centroids = np.array([np.array(rr.centroid) for rr in rp1])
    centroids = centroids.astype(np.uint16)
    ratio_plot[centroids[:,0], centroids[:,1], centroids[:,2]] = ratios
#    tiff.imshow(dilate(np.max(ratio_plot, axis=0), size=5).astype(np.float32))

    centroid_dir = os.path.join(outdir, 'centroids')
    mkdir(centroid_dir)
    oname = os.path.join(centroid_dir, os.path.basename(fname))[:-4] + '_centroids.tif'
    tiff.imsave(oname, dilate(np.max(ratio_plot, axis=0), size=5).astype(np.float32))

    # Plot full nuclei
    ratio_plot = np.zeros(lab_img.shape)
    for ii, reg in enumerate(rp1):
        ratio_plot[lab_img == reg.label] = ratios[ii]

    full_dir = os.path.join(outdir, 'full')
    mkdir(full_dir)
    oname = os.path.join(full_dir, os.path.basename(fname))[:-4] + '_full.tif'
    fp.tiffsave(oname, ratio_plot.astype(np.float32), resolution=resolution)
    fp.tiffsave(oname[:-4] + '_maxproj.tif', np.array([np.max(ratio_plot.astype(np.float32), axis=0)]), resolution=resolution)

################################################################################

#    tiff.imshow(ratio_plot)
#    tiff.imshow(lab_img)