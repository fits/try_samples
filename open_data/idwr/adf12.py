
import sys
import pandas as pd
from statsmodels.tsa import stattools

data_file = sys.argv[1]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

d = df.groupby(['year', 'week']).sum().iloc[:, 12]

params = ['ctt', 'ct', 'c', 'nc']
lag = 60

for c in params:
	r = stattools.adfuller(d.values, maxlag = lag, regression = c)
	print(f"{c}, p-value={r[1]}, {r}")

print('----- diff -----')

dd = d.diff().dropna()

for c in params:
	r = stattools.adfuller(dd.values, maxlag = lag, regression = c)
	print(f"{c}, p-value={r[1]}, {r}")
