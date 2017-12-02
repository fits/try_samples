
import sys
import pandas as pd
import statsmodels.api as sm
from functools import reduce

data_file = sys.argv[1]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

ds = df.groupby(['year', 'week']).sum().iloc[:, 1]

d = ds.astype('float').values

def sarima(param):
	try:
		sa = sm.tsa.SARIMAX(
			d,
			order = param[0],
			seasonal_order = param[1],
			trend = 'n'
		).fit()

		res = (sa.aic, sa)
	except:
		res = None

	return res

params = [((p, d, q), (sp, sd, sq, 52)) 
      for p in range(2)
      for d in range(2)
      for q in range(2)
      for sp in range(2)
      for sd in range(2)
      for sq in range(2)]

r = reduce(
	lambda a, b: a if a[0] <= b[0] else b,
	filter(
		lambda a: a is not None, 
		map(sarima, params)
	)
)

print(r[1].summary())
