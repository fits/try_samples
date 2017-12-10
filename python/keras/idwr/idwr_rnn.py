import sys
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from functools import reduce
from keras.models import Sequential
from keras.layers.core import Dense, Activation
from keras.layers.recurrent import GRU
from keras.optimizers import Adam

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_file = sys.argv[3]

window_size = 5
epoch = 200
batch_size = 4
n_num = 30

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

dv = df.groupby(['year', 'week'])[item_name].sum().astype('float').values

def window(d, wsize):
    return [d[i:(i + wsize)].flatten() for i in range(len(d) - wsize + 1)]

dw = window(dv, window_size)

data = np.array([i[0:-1] for i in dw]).reshape(len(dw), window_size - 1, 1)
labels =np.array([i[-1] for i in dw]).reshape(len(dw), 1)

model = Sequential()

model.add(
    GRU(n_num, activation = 'relu', input_shape = (window_size - 1, 1))
)

model.add(Dense(1))
model.add(Activation('linear'))

opt = Adam()

model.compile(loss = 'mean_squared_error', optimizer = opt)

model.fit(data, labels, epochs = epoch, batch_size = batch_size)

res1 = model.predict(data)

def predict(a, b):
    r = model.predict(a[1])

    return (
        np.append(a[0], r), 
        np.append(a[1][:, 1:], np.array([r]), axis = 1)
    )

fst_data = np.array(dw[0][0:-1]).reshape(1, window_size - 1, 1)
res2 = reduce(predict, range(len(dv) - window_size + 1), (np.array([]), fst_data))

plt.plot(dv)

for r in [res1, res2[0]]:
    plt.plot(range(window_size - 1, len(r) + window_size - 1), r)

plt.savefig(dest_file)
