# coding: utf-8

import sys
import pandas as pd
import matplotlib.pyplot as plt

data_file = sys.argv[1]
img_file = sys.argv[2]

df = pd.read_csv(data_file, parse_dates = ['lastdate'])

df.groupby('lastdate').sum().iloc[:, 1:20].plot(legend = False)

plt.savefig(img_file)
