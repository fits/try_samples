
import sys
import pandas as pd
import matplotlib.pyplot as plt
import statsmodels.api as sm

data_file = sys.argv[1]
dest_file = sys.argv[2]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

ds = df.groupby(['year', 'week']).sum().iloc[:, 1]

d = ds.astype('float').values

d_sarima = sm.tsa.SARIMAX(
    d,
    order=(1, 0, 1),
    seasonal_order=(1, 1, 0, 52),
    trend='n'
)

res = d_sarima.fit()

print(res.summary())

plt.plot(d)
plt.plot(res.predict(0, 250))

plt.savefig(dest_file)
