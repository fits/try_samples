import sys
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from datetime import datetime
from functools import reduce
from keras.models import Sequential
from keras.layers.core import Dense, Activation
from keras.layers.normalization import BatchNormalization
from keras.layers.recurrent import GRU
from keras.optimizers import Nadam

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_file = sys.argv[3]
predict_size = int(sys.argv[4])
batch_size = int(sys.argv[5])

t = 4
epoch = 5000
n_num = 80

df = pd.read_csv(data_file, encoding = 'UTF-8')

ds = df.groupby(['year', 'week'])[item_name].sum()

def window_with_index(d, wsize):
    return [
        np.r_[
            d.index[i + wsize - 1][0], 
            d.index[i + wsize - 1][1], 
            d[i:(i + wsize)].values.flatten()
        ] for i in range(len(d) - wsize + 1)
    ]

dw = window_with_index(ds.astype('float'), t + 1)

input_num = len(dw[0]) - 1

data = np.array([i[0:-1] for i in dw]).reshape(len(dw), input_num, 1)
labels = np.array([i[-1] for i in dw]).reshape(len(dw), 1)

model = Sequential()

model.add(BatchNormalization(axis = 1, input_shape = (input_num, 1)))

model.add(GRU(n_num, activation = 'relu'))

model.add(Dense(1))
model.add(Activation('linear'))

opt = Nadam()

model.compile(loss = 'mean_squared_error', optimizer = opt)

hist = model.fit(data, labels, epochs = epoch, batch_size = batch_size)

fig, axes = plt.subplots(1, 2, figsize = (12, 6))

axes[0].plot(hist.history['loss'])

axes[1].plot(ds.values, label = 'actual')

res1 = model.predict(data)

axes[1].plot(range(t, len(res1) + t), res1, label = 'predict1')

def weeks_of_year(year):
    return datetime(year, 12, 28).isocalendar()[1]

def predict(a, b):
    r = model.predict(a[1])

    year = a[1][0, 0, 0]
    week = a[1][0, 1, 0] + 1

    if week > weeks_of_year(int(year)):
        year += 1
        week = 1

    next = np.r_[
        year,
        week,
        a[1][:, 3:].flatten(),
        r.flatten()
    ].reshape(a[1].shape)

    return (np.append(a[0], r), next)

fst_data = data[0].reshape(1, input_num, 1)
res2, _ = reduce(predict, range(predict_size), (np.array([]), fst_data))

axes[1].plot(range(t, predict_size + t), res2, label = 'predict2')

min_year = min(ds.index.levels[0])
years = range(min_year, min_year + int(predict_size / 52) + 1)

axes[1].set_xticklabels(years)

axes[1].set_xticks(
    reduce(lambda a, b: a + [a[-1] + weeks_of_year(b)], years[:-1], [0])
)

axes[1].legend(bbox_to_anchor = (1, -0.1), ncol = 3)

fig.subplots_adjust(bottom = 0.15)

plt.savefig(dest_file)
