
import sys
import pandas as pd
from statsmodels.tsa import stattools

data_file = sys.argv[1]
item_name = sys.argv[2]
lag = int(sys.argv[3])

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

ds = df.groupby(['year', 'week'])[item_name].sum()

params = ['ctt', 'ct', 'c', 'nc']

for c in params:
	r = stattools.adfuller(ds.values, maxlag = lag, regression = c)
	print(f"{c}, p-value={r[1]}, {r}")

print('----- diff -----')

dd = ds.diff().dropna()

for c in params:
	r = stattools.adfuller(dd.values, maxlag = lag, regression = c)
	print(f"{c}, p-value={r[1]}, {r}")
