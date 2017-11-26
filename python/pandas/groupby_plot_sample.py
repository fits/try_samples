
import sys
import pandas as pd
import matplotlib.pyplot as plt

data_file = sys.argv[1]
dest_dir = sys.argv[2]

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

ds = df.groupby(['year', 'week']).sum().iloc[:, 1:]

for col in ds:
    plt.figure()
    ds[col].plot()
    plt.savefig(f"{dest_dir}/{col}.png")
