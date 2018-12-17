#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Mon Sep 17 14:15:20 2018

@author: henrik
"""

import numpy as np
import pandas as pd
from phenotastic.misc import listdir
import seaborn as sns
import matplotlib.pyplot as plt

from scipy.stats import normaltest
from scipy.stats import sem
import scipy

################################################################################
### READ DATA
################################################################################
outdir = '/home/henrik/out_bhatia/'
files = listdir(outdir, include='.dat')
files.sort()

frame = pd.concat([pd.read_csv(file_, index_col=None, header='infer', sep='\t') for file_ in files])

################################################################################
### FILTERING
################################################################################
frame = frame[frame['size'] > 100]
frame = frame[frame['size'] < 1500]
#frame = frame[frame['mean_ratio'] < 1.0]
#frame = frame[frame['mean_ratio'] > .05]
#frame = frame[frame['DII_max'] > 1000]

################################################################################
### STATISTICS
################################################################################
ab = frame[frame['side'] == 'ab']
ad = frame[frame['side'] == 'ad']
ad = ad.sort_values(by='mean_ratio', ascending=True)
ab = ab.sort_values(by='mean_ratio', ascending=True)

#thres = 900
print normaltest(ab.mean_ratio).pvalue
print normaltest(ad.mean_ratio).pvalue
print np.mean(ad.mean_ratio), np.std(ad.mean_ratio), sem(ad.mean_ratio)
print np.mean(ab.mean_ratio), np.std(ab.mean_ratio), sem(ab.mean_ratio)
#print scipy.stats.spearmanr(ad[:thres].mean_ratio.values, ab[:thres].mean_ratio.values)
print scipy.stats.ks_2samp(ad.mean_ratio, ab.mean_ratio)
print scipy.stats.mannwhitneyu(ad.mean_ratio, ab.mean_ratio, use_continuity=True, alternative='two-sided')
################################################################################
### PLOTTING
################################################################################
''' Set Seaborn plot style '''
sns.set(style='whitegrid', font_scale=2,
        rc={'axes.grid': False,
              'figure.figsize': (7., 5.)})

plt.style.use('seaborn-whitegrid')
palette = plt.get_cmap('Set1')

''' Generate ranked cell comparison plot '''
import matplotlib.patches as mpatches
patchList = []
legend_dict = { 'Adaxial' : palette(1), 'Abaxial' : palette(2)}
for key in legend_dict:
        data_key = mpatches.Patch(color=legend_dict[key], label=key)
        patchList.append(data_key)

plt.plot((ad.mean_ratio.values), color=palette(1), linewidth=4)
plt.plot((ab.mean_ratio.values), color=palette(2), linewidth=4)
plt.legend(handles=patchList, loc=2, ncol=1)
plt.xlabel('Ranked cell index')
plt.ylabel('Mean intensity ratio')

''' '''
ranked_data = frame.sort_values(by='mean_ratio')[['side', 'mean_ratio']]
side = ranked_data['side'].values.astype('str')
vals = ranked_data['mean_ratio'].values
a = np.zeros(len(side) + 1)
b = np.zeros(len(side) + 1)
for ii, s in enumerate(side, start=1):
    if s == 'ad':
        a[ii] = a[ii - 1] + 1
        b[ii] = b[ii - 1]
    else:
        a[ii] = a[ii - 1]
        b[ii] = b[ii - 1] + 1

a = a / max(a)
b = b / max(b)

sns.set(style='whitegrid', font_scale=2,
        rc={'axes.grid': False,
              'figure.figsize': (7., 5.)})

plt.style.use('seaborn-whitegrid')
palette = plt.get_cmap('Set1')

fig = plt.figure()
ax = fig.add_subplot(111)
ax.step(b, a, color=palette(1), linewidth=2)
ax.plot(np.linspace(0, 1, 100), np.linspace(0, 1, 100), color='gray', linewidth=2, linestyle='--')
plt.xlabel('Abaxial ranked fraction')
plt.ylabel('Adaxial ranked fraction')
from sklearn.metrics import auc
ax.text(.05,.9, 'AUC = {0:.3g}'.format(auc(b, a, True)), fontsize=24)
print auc(b, a, True)
ax.locator_params(axis='y', nbins=5)
ax.locator_params(axis='x', nbins=5)
fig.subplots_adjust(left=.15, bottom=.18)
fig.savefig('AUC_comparison.png')
fig.savefig('AUC_comparison.pdf')

''' Generate adaxial-abaxial overall distribution comparison plot '''
sns.set(style='whitegrid', font_scale=2,
        rc={'axes.grid': False,
              'figure.figsize': (7., 5.)})

plt.style.use('seaborn-whitegrid')
palette = plt.get_cmap('Set1')
ax = sns.violinplot(x="side", y="mean_ratio", data=frame,
                    inner='box', width=.8, linewidth=2, palette='muted', bw=.2, cut=0)
ax.set(xticklabels=['Abaxial', 'Adaxial'])
plt.xlabel('')
plt.ylabel('Mean intensity ratio')
plt.savefig('adab_violins.png')
plt.savefig('adab_violins.pdf')

''' Generate sample-sample distribution comparison figure '''
sns.set(style='whitegrid', font_scale=2,
        rc={'axes.grid': False,
              'figure.figsize': (12., 6.)})

plt.style.use('seaborn-whitegrid')
palette = plt.get_cmap('Set1')

ax = sns.violinplot(x="side", y="median_ratio", hue='sample', data=frame,
                    inner=None, opacity=.5, cut=0, palette='muted', width=.8)
ax = sns.swarmplot(x='side', y='max_ratio', hue='sample',
                   data=frame, size=2, linewidth=2, dodge=True)
handles, labels = ax.get_legend_handles_labels()
plt.legend(handles[0:10], labels[0:10], bbox_to_anchor=(.99, .97),
           loc=2, borderaxespad=.1, title='Sample')
ax.set(xticklabels=['Abaxial', 'Adaxial'])
plt.xlabel('')
plt.ylabel('Mean intensity ratio')
plt.savefig('adab_violins_all.png')
plt.savefig('adab_violins_all.pdf')
