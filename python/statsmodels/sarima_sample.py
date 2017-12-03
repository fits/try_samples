
import sys
import pandas as pd
import matplotlib.pyplot as plt
import statsmodels.api as sm
from functools import reduce

data_file = sys.argv[1]
item_name = sys.argv[2]
season = int(sys.argv[3])
dest_file = sys.argv[4]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

dv = df.groupby(['year', 'week'])[item_name].sum().astype('float').values

def sarima_fit(param):
    try:
        return sm.tsa.SARIMAX(
            dv,
            order = param[0],
            seasonal_order = param[1],
            trend = 'n'
        ).fit()
    except:
        return None

params = [
    ((p, d, q), (sp, sd, sq, season))
    for p  in range(3)
    for d  in range(2)
    for q  in range(2)
    for sp in range(2)
    for sd in range(2)
    for sq in range(2)
]

r = reduce(
    lambda a, b: a if a.aic <= b.aic else b,
    filter(
        lambda a: a is not None,
        map(sarima_fit, params)
    )
)

print(r.summary())

plt.plot(dv)

plt.plot(r.predict(0, 250))

plt.savefig(dest_file)
