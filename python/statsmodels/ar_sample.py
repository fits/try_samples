
import sys
import pandas as pd
import matplotlib.pyplot as plt
from statsmodels.tsa.ar_model import AR
from functools import reduce

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_file = sys.argv[3]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

dv = df.groupby(['year', 'week'])[item_name].sum().values

def ar_fit(p):
	return (p, AR(dv).fit(p))

r = reduce(
    lambda a, b: a if a[1].aic <= b[1].aic else b,
    map(ar_fit, range(1, 3))
)

print(f"p = {r[0]}, aic = {r[1].aic}")

plt.plot(dv)

start = r[0]

plt.plot(range(start, 251), r[1].predict(start, 250))

plt.savefig(dest_file)
