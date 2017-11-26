
import sys
import pandas as pd
import matplotlib.pyplot as plt
from statsmodels.tsa import stattools

data_file = sys.argv[1]
item_name = sys.argv[2]
lag_num = int(sys.argv[3])
dest_file = sys.argv[4]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

ds = df.groupby(['year', 'week'])[item_name].sum()

fig, axes = plt.subplots(2, 1)

r1 = stattools.acf(ds, nlags = lag_num)

axes[0].bar(range(len(r1)), r1, width = 0.1)

r2 = stattools.pacf(ds, nlags = lag_num)

axes[1].bar(range(len(r2)), r2, width = 0.1)

plt.savefig(dest_file)
