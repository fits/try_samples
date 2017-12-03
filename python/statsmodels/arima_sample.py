
import sys
import pandas as pd
import matplotlib.pyplot as plt
from statsmodels.tsa.arima_model import ARIMA
from functools import reduce

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_file = sys.argv[3]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

dv = df.groupby(['year', 'week'])[item_name].sum().astype('float').values

def arima_fit(param):
    try:
        res = ARIMA(dv, order = param).fit()
        return (param, res.aic, res)
    except:
        return None

params = [
    (p, d, q)
    for p in range(3)
    for d in range(2)
    for q in range(3)
]

r = reduce(
    lambda a, b: a if a[1] <= b[1] else b,
    filter(
        lambda a: a is not None,
        map(arima_fit, params)
    )
)

print(f"param = {r[0]}, aic = {r[1]}")

plt.plot(dv)

start = r[0][1]

plt.plot(range(start, 251), r[2].predict(start, 250, typ = 'levels'))

plt.savefig(dest_file)
