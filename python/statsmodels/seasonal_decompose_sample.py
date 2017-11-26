
import sys
import pandas as pd
import matplotlib.pyplot as plt
from statsmodels.tsa.seasonal import seasonal_decompose

data_file = sys.argv[1]
item_name = sys.argv[2]
season_freq = int(sys.argv[3])
dest_file = sys.argv[4]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

ds = df.groupby(['year', 'week'])[item_name].sum()

ds.plot()

ts = seasonal_decompose(ds.values, freq = season_freq)

plt.plot(ts.trend)
plt.plot(ts.seasonal)
plt.plot(ts.resid)

plt.savefig(dest_file)
