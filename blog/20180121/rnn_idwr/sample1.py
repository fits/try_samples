import sys
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from functools import reduce
from keras.models import Sequential
from keras.layers.core import Dense, Activation
from keras.layers.recurrent import GRU
from keras.optimizers import Nadam

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_file = sys.argv[3]

t = 4
epoch = 3000
batch_size = 52
n_num = 80

df = pd.read_csv(data_file, encoding = 'UTF-8')

ds = df.groupby(['year', 'week'])[item_name].sum()

def window(d, wsize):
    return [d[i:(i + wsize)].values.flatten() for i in range(len(d) - wsize + 1)]

dw = window(ds.astype('float'), t + 1)

data = np.array([i[0:-1] for i in dw]).reshape(len(dw), t, 1)
labels = np.array([i[-1] for i in dw]).reshape(len(dw), 1)

model = Sequential()

model.add(GRU(n_num, activation = 'relu', input_shape = (t, 1)))

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

def predict(a, b):
    r = model.predict(a[1])

    return (
        np.append(a[0], r), 
        np.append(a[1][:, 1:], np.array([r]), axis = 1)
    )

fst_data = data[0].reshape(1, t, 1)
res2, _ = reduce(predict, range(len(ds) - t), (np.array([]), fst_data))

axes[1].plot(range(t, len(res2) + t), res2, label = 'predict2')

axes[1].legend()

plt.savefig(dest_file)
