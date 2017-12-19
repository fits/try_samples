from bokeh.plotting import figure
from bokeh.io import save, output_file

output_file('line_sample.html')

p = figure()

p.line([1, 2, 3], [6, 2, 8])

save(p, title = 'line sample')
