
import sys
import json
import codecs
import pandas as pd
import seaborn as sns
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

df = pd.DataFrame(list(map(convert, data['log']['entries'])))

g = sns.PairGrid(df, hue = 'type', x_vars = ['time'], y_vars = ['body-size'], size = 10)

g.map(plt.scatter, linewidths = 8).add_legend()

imgfile = "%s.png" % sys.argv[1]

plt.savefig(imgfile)
