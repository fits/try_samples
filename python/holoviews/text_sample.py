import sys
import holoviews as hv

dest_file = sys.argv[1]

hv.extension('bokeh')
hv.util.output('size = 150')

p = hv.Overlay([
    hv.Scatter((0.5, 0.8)) * hv.Text(0.5, 0.8, 'item1'),
    hv.Scatter((0.7, 1.0)) * hv.Text(0.7, 1.0, 'item2'),
    hv.Scatter((0.4, 0.2)) * hv.Text(0.4, 0.2, 'item3'),
    hv.Scatter((0.0, 0.0)) * hv.Text(0.0, 0.0, 'item4'),
    hv.Scatter((1.0, 1.0)) * hv.Text(1.0, 1.0, 'item5')
])

hv.renderer('bokeh').save(p, dest_file)
