
import sys
import pandas as pd
import matplotlib.pyplot as plt
from statsmodels.tsa.arima_model import ARIMA

data_file = sys.argv[1]
dest_file = sys.argv[2]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

d = df.groupby(['year', 'week']).sum().iloc[:, 12].astype('float').values

d_arima = ARIMA(d, order = (3, 1, 0))

res = d_arima.fit()

print(res.summary())

plt.plot(d)
plt.plot(res.predict(1, 250, typ = 'levels'))

plt.savefig(dest_file)
