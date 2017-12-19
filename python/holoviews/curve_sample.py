import sys
import holoviews as hv

dest_file = sys.argv[1]

hv.extension('bokeh')
hv.util.output('size = 150')

p = hv.Overlay([
    hv.Curve(([1, 2, 3], [1, 3, 10]), label = 'item1'),
    hv.Curve(([1, 2, 3], [6, 4, 9]), label = 'item2'),
])

hv.renderer('bokeh').save(p, dest_file)
