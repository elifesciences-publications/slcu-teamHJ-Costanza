#!/usr/bin/env python2
# -*- coding: utf-8 -*-
import numpy as np
import tifffile as tiff
from pycostanza.steepest import get_footprint, steepest_ascent
from pycostanza.labels import erode, dilate, merge_labels_distance, remove_labels_intensity
from pycostanza.labels import remove_labels_size, relabel, merge_labels_depth, merge_labels_small2closest
from scipy.ndimage.filters import gaussian_filter, median_filter
from skimage.morphology import binary_closing, binary_opening
import mahotas as mh
from skimage.morphology import remove_small_objects
from skimage.measure import regionprops
from skimage.exposure import rescale_intensity
import os

import phenotastic.file_processing as fp
from phenotastic.misc import autocrop, listdir, mkdir

### Import example inage
fname = '/home/henrik/data/from-marcus/R2D2 4DAS segmentation and quantification trial_for sending to Henrik_20 august 2018.lif - 5.3 4DAS-manually_cropped_deconv.tif'

f = fp.tiffload(fname)
f.data = autocrop(f.data, fct=np.max, threshold=100)
f.data = f.data.astype(np.float64)
resolution = fp.get_resolution(f)

int_img = f.data.copy()
int_img[:, 1] /= np.max(int_img[:, 1])
int_img[:, 2] /= np.max(int_img[:, 2])
int_img = np.max(int_img[:, 1:], axis=1)
int_img *= np.iinfo(np.uint16).max
int_img = int_img.astype(np.uint16)
pre_mask_int_img = int_img.copy()

### Preprocessing
footprint = get_footprint(3, 2)
mask = int_img < 1. * mh.otsu(int_img, True)
mask = remove_small_objects(mask, min_size=200, connectivity=2)
int_img[mask] = 0

### Smooth signal
smooth_img = int_img.copy()
smooth_img = median_filter(smooth_img, size=1, footprint=get_footprint(3, 2))
smooth_img = gaussian_filter(smooth_img, sigma=[.5, 1, 1])
smooth_img = gaussian_filter(smooth_img, sigma=[.5, 1, 1])
smooth_img = gaussian_filter(smooth_img, sigma=[.5*2/3, 1*2/3, 1*2/3])
smooth_img = gaussian_filter(smooth_img, sigma=[.5*1/3, 1*1/3, 1*1/3])

### Perform initial segmentation
lab_img = steepest_ascent(smooth_img, resolution, connectivity=2, mask=np.logical_not(mask))

### Merge and remove labels
lab_img = merge_labels_distance(lab_img, smooth_img, threshold=1.5, resolution=resolution)
lab_img = merge_labels_small2closest(lab_img, threshold=25, distance_upper_bound=3., resolution=None)
lab_img = remove_labels_size(lab_img, min_size=200, max_size=None, resolution=None)
#        lab_img = merge_labels_depth(lab_img, int_img, threshold=2000., connectivity=2)
lab_img = remove_labels_intensity(lab_img, int_img, threshold=3000.)
lab_img = remove_labels_intensity(lab_img, f.data[:, 1], threshold=20.)

lab_img = relabel(lab_img)

####
import matplotlib.pyplot as plt
import seaborn as sns
#from mpl_toolkits.axes_grid1.inset_locator import InsetPosition
sns.set(style='whitegrid', font_scale=2,
        rc={'axes.grid': True,
              'figure.figsize': (10., 5.)})

plt.style.use('seaborn-whitegrid')
palette = plt.get_cmap('Set1')
CBAR_PAD = 0
TITLE_PAD = 10


fig, (ax1, ax2, ax3) = plt.subplots(ncols=3, sharex=True, sharey=True)
fig.subplots_adjust(wspace=0.05)
im = sns.heatmap(int_img[15], ax=ax1, cbar_kws = dict(use_gridspec=False, location="top", pad=-CBAR_PAD), mask=int_img[15]==0)
im.collections[0].colorbar.ax.set_xticklabels(list(im.collections[0].colorbar.ax.get_xticklabels()), rotation=90)
im.set_title('Intensity', pad=TITLE_PAD)
fp.tiffsave('int_img.tif', data=int_img, resolution=fp.get_resolution(f), metadata=f.metadata)

#im.collections[0].colorbar.set_label('Lol')

#im2 = sns.heatmap(smooth_img[15], ax=ax2, cbar_kws = dict(use_gridspec=False, location="top"))
#im2.collections[0].colorbar.ax.set_xticklabels(list(im2.collections[0].colorbar.ax.get_xticklabels()), rotation=90)
#im2.set_title('Label', pad=150)
#im2.collections[0].colorbar.set_label('Lol')

im2 = sns.heatmap(lab_img[15], ax=ax2, cbar_kws = dict(use_gridspec=False, location="top", pad=CBAR_PAD), mask=lab_img[15]==0)
im2.collections[0].colorbar.ax.set_xticklabels(list(im2.collections[0].colorbar.ax.get_xticklabels()), rotation=90)
im2.set_title('Label', pad=TITLE_PAD)
fp.tiffsave('lab_img.tif', data=lab_img.astype(np.uint16), resolution=fp.get_resolution(f), metadata=f.metadata)


#im3.collections[0].colorbar.set_label('Lol')

#plt.subplots_adjust(wspace=None, hspace=None)
ax1.set_ylabel('Y Coordinate')
ax2.set_xlabel('X Coordinate')

''' DATA COLLECTION '''
### Normalise data according to mDII expression. Assume max values the same
diimax = np.max(mh.labeled.labeled_max(f.data[:, 1], lab_img))
mdiimax = np.max(mh.labeled.labeled_max(f.data[:, 2], lab_img))
f = fp.tiffload(fname)
f.data = autocrop(f.data, fct=np.max, threshold=100)
f.data = f.data.astype(np.float64)
f.data[:, 1] *= diimax / mdiimax / mdiimax
f.data[:, 2] /= mdiimax

rp1 = regionprops(lab_img, f.data[:, 1])
rp2 = regionprops(lab_img, f.data[:, 2])
ratios = [rp1[ii].mean_intensity / rp2[ii].mean_intensity for ii in xrange(len(rp1))]

ratio_plot_full = np.zeros(lab_img.shape)
for ii, reg in enumerate(rp1):
    ratio_plot_full[lab_img == reg.label] = ratios[ii]

im3 = sns.heatmap(ratio_plot_full[15], ax=ax3, cbar_kws = dict(use_gridspec=False, location="top", pad=CBAR_PAD), mask=ratio_plot_full[15]==0)
im3.collections[0].colorbar.ax.set_xticklabels(list(im3.collections[0].colorbar.ax.get_xticklabels()), rotation=90)
fp.tiffsave('ratio_img.tif', data=ratio_plot_full.astype(np.float32), resolution=fp.get_resolution(f), metadata=f.metadata)

#im.collections[0].colorbar.formatter.set_scientific((-2,4))
#im.collections[0].colorbar.ax.get_xaxis().set_major_formatter(ticker.FuncFormatter(fmt))
#im3.collections[0].colorbar.ax.ticklabel_format(style='sci', scilimits=(-3,4),axis='both')
#plt.ticklabel_format(style='sci', axis='x', scilimits=(0,0))
im3.set_title('Intensity ratio', pad=TITLE_PAD)

fig.subplots_adjust(bottom=0.2, top=.67)
plt.locator_params(axis='y', nbins=6)
plt.locator_params(axis='x', nbins=6)

''' Plot centroids '''
ratio_plot = np.zeros(lab_img.shape)
centroids = np.array([np.array(rr.centroid) for rr in rp1])
centroids = centroids.astype(np.uint16)
ratio_plot[centroids[:, 0], centroids[:, 1], centroids[:, 2]] = ratios
#ratio_plot = dilate(np.max(ratio_plot, axis=0), size=5).astype(np.float32)

sns.set(style='whitegrid', font_scale=2,
        rc={'axes.grid': False,
              'figure.figsize': (7., 5.)})

fig = plt.figure()
im = sns.heatmap(ratio_plot, cbar_kws = dict(use_gridspec=False, location="right"), mask=ratio_plot==0)
plt.locator_params(axis='y', nbins=6)
plt.locator_params(axis='x', nbins=6)
plt.xlabel('X coordinate')
plt.ylabel('Y coordinate')
fig.subplots_adjust(left=.15, bottom=.2, right=.75)


import seaborn as sns
#sns.heatmap(ratio_plot, mask=ratio_plot==0)

#    tiff.imshow(dilate(np.max(ratio_plot, axis=0), size=5).astype(np.float32))

### Plot full nuclei
#        tiff.imshow(ratio_plot_full)
