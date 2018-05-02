
import sys
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import pandas as pd
import pandas.tseries.offsets as offsets
import statsmodels.api as sm

data_file = sys.argv[1]
item_name = sys.argv[2]
type = sys.argv[3]
dest_file = sys.argv[4]
season = int(sys.argv[5]) if len(sys.argv) > 5 else None

df = pd.read_csv(data_file, parse_dates = ['lastdate'])

ds = df.groupby(['lastdate'])[item_name].sum().astype('float')

r = sm.tsa.UnobservedComponents(ds, type, seasonal = season).fit()

print(r.summary())

fig, ax = plt.subplots()

plt.plot(ds)

start_date = max(ds.index)

plt.plot( r.predict(start_date, start_date + offsets.Day(350)) )

ax.xaxis.set_major_locator(mdates.YearLocator())

plt.savefig(dest_file)
