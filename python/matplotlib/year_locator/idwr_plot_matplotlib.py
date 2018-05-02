# coding: utf-8

import sys
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.dates as mdates

data_file = sys.argv[1]
item_name = sys.argv[2]
img_file = sys.argv[3]

df = pd.read_csv(data_file, parse_dates = ['lastdate'])

dv = df.groupby('lastdate')[item_name].sum().astype('float')

fig, ax = plt.subplots()

plt.plot(dv)

ax.xaxis.set_major_locator(mdates.YearLocator())

plt.savefig(img_file)
