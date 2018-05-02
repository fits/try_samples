
import pandas as pd
import pandas.tseries.offsets as offsets

dt = pd.to_datetime('2018-05-01')

print(dt)

print(dt + offsets.Day(25))
