
import sys
import pandas as pd
import matplotlib.pyplot as plt
import statsmodels.api as sm
from functools import reduce

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_file = sys.argv[3]
season = int(sys.argv[4]) if len(sys.argv) > 4 else None

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

dv = df.groupby(['year', 'week'])[item_name].sum().astype('float').values

def dlm_fit(type):
    return sm.tsa.UnobservedComponents(dv, type, seasonal = season).fit()

types = [
    'local level',
    'local linear trend',
    'local linear deterministic trend',
    'random walk',
    'smooth trend',
    'random trend'
]

r = reduce(
    lambda a, b: a if a.aic <= b.aic else b,
    map(dlm_fit, types)
)

print(r.summary())

keys = ['level', 'trend', 'seasonal', 'cycle', 'autoregressive']

comps = [getattr(r, k).smoothed for k in keys if r.specification[k]]

plt.plot(dv)
plt.plot(sum(comps))

for c in comps:
    plt.plot(c)

plt.savefig(dest_file)
