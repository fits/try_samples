import sys
import os
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from keras.models import Sequential
from keras.layers.core import Dense, Activation
from keras.layers.normalization import BatchNormalization
from keras.layers.recurrent import GRU
from keras.optimizers import Nadam
from keras.callbacks import Callback
from keras import backend as K

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_img = sys.argv[3]
dest_model = sys.argv[4]

t = 4
epoch = 10000
batch_size = 52
n_num = 80
input_num = t + 2

class SampleStopper(Callback):
    def __init__(self, filepath, monitor = 'loss', 
        patience = 100, min_epochs = 1000):

        self.filepath = filepath
        self.monitor = monitor
        self.patience = patience
        self.min_epochs = min_epochs
        self.wait = 0
        self.best = None
        self.best_weights = None

    def on_epoch_end(self, epoch, logs = None):
        current = logs.get(self.monitor)

        if self.best is None or self.best > current:
            self.wait = 0
            self.best = current
            self.best_weights = self.model.get_weights()
        else:
            self.wait += 1

        if epoch >= self.min_epochs and self.wait >= self.patience:
            self.model.stop_training = True
            self.model.set_weights(self.best_weights)
            self.model.save(self.filepath, overwrite = True)

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

ds = df.groupby(['year', 'week'])[item_name].sum()

def window_with_index(d, size):
    return [
        np.r_[
            d.index[i + size][0], # year
            d.index[i + size][1], # week
            d[i:(i + size + 1)].values.flatten()
        ]
        for i in range(len(d) - size)
    ]

dw = window_with_index(ds.astype('float'), t)

data = np.array([i[0:-1] for i in dw]).reshape(len(dw), input_num, 1)
labels = np.array([i[-1] for i in dw]).reshape(len(dw), 1)


model = Sequential()

model.add(BatchNormalization(axis = 1, input_shape = (input_num, 1)))

model.add(GRU(n_num, activation = 'relu'))

model.add(Dense(1))
model.add(Activation('linear'))

opt = Nadam()

model.compile(loss = 'mean_squared_error', optimizer = opt)

stopper = SampleStopper(dest_model, patience = 1000, min_epochs = 3000)

hist = model.fit(data, labels, epochs = epoch, batch_size = batch_size, 
                 callbacks = [stopper])

plt.plot(hist.history['loss'])

plt.savefig(dest_img)
