# coding: utf-8

import sys
import pandas as pd
import holoviews as hv

hv.extension('bokeh')

data_file = sys.argv[1]
dest_file = sys.argv[2]

df = pd.read_csv(data_file, parse_dates = ['lastdate'])

dg = df.groupby('lastdate').sum().iloc[:, 1:20]

plist = [hv.Curve(dg[c].reset_index().values, label = f"'{c}'") for c in dg]

p = hv.Overlay(plist)

p = p.opts(plot = dict(width = 800, height = 600, fontsize = 8))
p = p.redim.label(x = 'lastdate', y = 'num')

hv.renderer('bokeh').save(p, dest_file)
