
import sys
import pandas as pd
import holoviews as hv

hv.extension('bokeh')

dest_file = sys.argv[1]

df = pd.read_csv('data.csv')

plot_params = dict(color_index = 'category')
style_params = dict(cmap = 'viridis', size = 10)

p = hv.Scatter(df, 'num', vdims=['value', 'category']).opts(plot = plot_params, style = style_params)

hv.renderer('bokeh').save(p, dest_file)
