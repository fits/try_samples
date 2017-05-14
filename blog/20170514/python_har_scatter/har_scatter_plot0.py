
import sys
import json
import codecs
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

data = json.load(codecs.open(sys.argv[1], 'r', 'utf-8'))

to_subtype = lambda mtype: mtype.split(';')[0].split('/')[-1]

convert = lambda d: {
    'subType': to_subtype(d['response']['content']['mimeType']),
    'bodySize': d['response']['bodySize'],
    'time': d['time']
}

df = pd.DataFrame(list(map(convert, data['log']['entries'])))

g = sns.PairGrid(df, hue = 'subType', x_vars = ['time'], y_vars = ['bodySize'], size = 10)

g.map(plt.scatter).add_legend()

imgfile = "%s.png" % sys.argv[1]

plt.savefig(imgfile)
