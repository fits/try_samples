
import sys
import json
import codecs
import pandas as pd
import matplotlib.pyplot as plt

data = json.load(codecs.open(sys.argv[1], 'r', 'utf-8'))

to_subtype = lambda mtype: mtype.split(';')[0].split('/')[-1]

convert = lambda d: {
    'subType': to_subtype(d['response']['content']['mimeType']),
    'bodySize': d['response']['bodySize'],
    'time': d['time']
}

color_map = {
    'javascript': 'blue',
    'x-javascript': 'blue',
    'json': 'green',
    'gif': 'tomato',
    'jpeg': 'red',
    'png': 'pink',
    'html': 'lime',
    'css': 'turquoise'
}

df = pd.DataFrame(list(map(convert, data['log']['entries'])))

ax = plt.figure().add_subplot(1, 1, 1)

for (k, c) in color_map.items():
    sdf = df[df['subType'] == k]

    if not sdf.empty:
        sdf.plot('time', 'bodySize', 'scatter', ax, c = c, label = k)

imgfile = "%s_2.png" % sys.argv[1]

plt.savefig(imgfile)
