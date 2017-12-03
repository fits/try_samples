
import sys
import pandas as pd
import matplotlib.pyplot as plt
from statsmodels.tsa.ar_model import AR
from functools import reduce

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_file = sys.argv[3]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

d = df.groupby(['year', 'week'])[item_name].sum().values

ar = AR(d)

def ar_fit(p):
    res = ar.fit(p)
    return (p, res.aic, res)

r = reduce(
    lambda a, b: a if a[1] <= b[1] else b,
    map(ar_fit, range(1, 3))
)

print(f"p = {r[0]}, aic = {r[1]}")

plt.plot(d)
plt.plot(range(r[0], 251), r[2].predict(r[0], 250))

plt.savefig(dest_file)
