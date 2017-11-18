
import functools
import glob
import os
import re
import sys
import pandas as pd

data_files = '%s/*.csv' % sys.argv[1]
dest_file = sys.argv[2]

r = re.compile('([0-9]+)-([0-9]+)-')

cols = [i for i in range(38) if i == 0 or i % 2 == 1]

conv = lambda x: 0 if x == '-' else int(x)
conv_map = {i:conv for i in range(len(cols)) if i > 0}

dfs = []

for f in glob.glob(data_files):
    d = pd.read_csv(f, encoding = 'Shift_JIS', skiprows = [0, 1, 3, 4],
                     usecols = cols, skipfooter = 1, converters = conv_map, 
                     engine = 'python')

    m = r.match(os.path.basename(f))

    d['year'] = m.group(1)
    d['week'] = m.group(2)

    dfs.append(d.rename(columns = {'Unnamed: 0': 'prefecture'}))

df = functools.reduce(lambda a, b: a.append(b), dfs)

df.to_csv(dest_file)
