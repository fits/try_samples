
import sys
import json
import codecs
import pandas as pd
import matplotlib.pyplot as plt

data = json.load(codecs.open(sys.argv[1], 'r', 'utf-8'))

to_type = lambda mtype: mtype.split(';')[0].split('/')[-1]

convert = lambda d: {
    'url': d['request']['url'],
    'method': d['request']['method'],
    'status': d['response']['status'],
    'body-size': d['response']['bodySize'],
    'mime-type': d['response']['content']['mimeType'],
    'type': to_type(d['response']['content']['mimeType']),
    'wait': d['timings']['wait'],
    'receive': d['timings']['receive'],
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
    d = df[df['type'] == k]

    if not d.empty:
        d.plot('time', 'body-size', 'scatter', ax, c = c, label = k)

imgfile = "%s.png" % sys.argv[1]

plt.savefig(imgfile)
