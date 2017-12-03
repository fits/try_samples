
import sys
import pandas as pd
from statsmodels.tsa import stattools

data_file = sys.argv[1]
item_name = sys.argv[2]
lag = int(sys.argv[3])

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

ds = df.groupby(['year', 'week'])[item_name].sum()

for d in [('orig', ds.values), ('diff', ds.diff().dropna())]:

    print(f"------ {d[0]} -----")

    for c in ['ctt', 'ct', 'c', 'nc']:

        r = stattools.adfuller(d[1], maxlag = lag, regression = c)

        print(f"{c}, p-value={r[1]}, {r}")

    print()
