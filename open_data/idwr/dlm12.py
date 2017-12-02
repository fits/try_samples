
import sys
import pandas as pd
import matplotlib.pyplot as plt
import statsmodels.api as sm

data_file = sys.argv[1]
dest_file = sys.argv[2]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

d = df.groupby(['year', 'week']).sum().iloc[:, 12].astype('float').values

d_dlm = sm.tsa.UnobservedComponents(d, 'local linear trend')

res = d_dlm.fit()

print(res.summary())

res.plot_components()

plt.savefig(dest_file)
