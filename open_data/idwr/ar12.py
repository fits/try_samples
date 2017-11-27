
import sys
import pandas as pd
import matplotlib.pyplot as plt
from statsmodels.tsa.ar_model import AR

data_file = sys.argv[1]
dest_file = sys.argv[2]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

d = df.groupby(['year', 'week']).sum().iloc[:, 12].astype('float').values

d_ar = AR(d)

res = d_ar.fit(3)

print(f"aic = {res.aic}")

plt.plot(d)
plt.plot(range(3, 251), res.predict(3, 250))

plt.savefig(dest_file)
