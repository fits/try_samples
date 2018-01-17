import sys
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from datetime import datetime
from functools import reduce
from keras.models import load_model

data_file = sys.argv[1]
item_name = sys.argv[2]
model_file = sys.argv[3]
dest_img = sys.argv[4]

t = 4
predict_max = 250
predict_size = predict_max - t
input_num = t + 2

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

ds = df.groupby(['year', 'week'])[item_name].sum()

model = load_model(model_file)

def predict(a, b):
    r = model.predict(a[1])

    y = a[1][0, 0, 0]
    w = a[1][0, 1, 0] + 1

    max_week = datetime(int(y), 12, 28).isocalendar()[1]

    if w > max_week:
        y += 1
        w = 1

    n = np.r_[ y, w, a[1][:, 3:].flatten(), r.flatten() ].reshape(1, input_num, 1)

    return (np.append(a[0], r), n)

fst_data = np.r_[ ds.index[t], ds[0:t].values.flatten() ].reshape(1, input_num, 1).astype('float')

pred = reduce(predict, range(predict_size), (np.array([]), fst_data))

plt.xlim(-10, predict_max)

min_year = min(ds.index.levels[0])
years = range(min_year, min_year + int(predict_max / 52) + 1)

plt.xticks(
    reduce(lambda a, b: a + [a[-1] + datetime(b, 12, 28).isocalendar()[1]], years[:-1], [0]), 
    years
)


plt.plot(ds.values, label = 'actual')

plt.plot(range(t, predict_max), pred[0], label = 'predict')

plt.legend()

plt.savefig(dest_img)
