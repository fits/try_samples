# coding: utf-8

import sys
import pandas as pd
import matplotlib.pyplot as plt

data_file = sys.argv[1]
item_name = sys.argv[2]
img_file = sys.argv[3]

df = pd.read_csv(data_file, parse_dates = ['lastdate'])

df.groupby('lastdate').sum()[item_name].plot(legend = False)

plt.savefig(img_file)
