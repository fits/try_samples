
import sys
import pandas as pd
import matplotlib.pyplot as plt
import statsmodels.api as sm

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_file = sys.argv[3]
season = int(sys.argv[4]) if len(sys.argv) > 4 else None

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

dv = df.groupby(['year', 'week'])[item_name].sum().astype('float').values

dlm = sm.tsa.UnobservedComponents(dv, 'local linear trend', seasonal = season)

r = dlm.fit()

print(r.summary())

r.plot_components()

plt.savefig(dest_file)
