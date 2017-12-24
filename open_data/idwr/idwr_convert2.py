# coding: utf-8

import codecs
import functools
import glob
import os
import re
import sys
import pandas as pd

data_files = f"{sys.argv[1]}/*.csv"
dest_file = sys.argv[2]

r = re.compile('"([0-9]+)年([0-9]+)週\(.*〜([0-9]+)月([0-9]+)日\)')

cols = [i for i in range(38) if i == 0 or i % 2 == 1]

conv = lambda x: 0 if x == '-' else int(x)
conv_map = {i:conv for i in range(len(cols)) if i > 0}

def read_info(file):
    f = codecs.open(file, 'r', 'Shift_JIS')
    f.readline()

    m = r.match(f.readline())

    f.close()

    year = int(m.group(1))
    week = int(m.group(2))
    last_year = year + 1 if week > 50 and m.group(3) == '01' else year

    return (year, week, f"{last_year}-{m.group(3)}-{m.group(4)}")

def read_csv(file):
    d = pd.read_csv(file, encoding = 'Shift_JIS', skiprows = [0, 1, 3, 4],
                     usecols = cols, skipfooter = 1, converters = conv_map, 
                     engine = 'python')

    info = read_info(file)

    d['year'] = info[0]
    d['week'] = info[1]
    d['lastdate'] = info[2]

    return d.rename(columns = {'Unnamed: 0': 'prefecture'})


dfs = [read_csv(f) for f in glob.glob(data_files)]

df = functools.reduce(lambda a, b: a.append(b), dfs)

df.to_csv(dest_file)
