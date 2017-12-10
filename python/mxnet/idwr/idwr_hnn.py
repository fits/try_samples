# coding: utf-8
import sys
import pandas as pd
import mxnet as mx
import numpy as np

import matplotlib

matplotlib.use('Agg')

import matplotlib.pyplot as plt

data_file = sys.argv[1]
item_name = sys.argv[2]
dest_file = sys.argv[3]

window_size = 53
epoch = 100
n_num = 20

df = pd.read_csv(data_file, encoding = 'Shift_JIS')

dv = df.groupby(['year', 'week'])[item_name].sum().astype('float64').values

def window(d, size):
    return [ d[i:(i + size)].flatten() for i in range(len(d) - size + 1) ]

dw = window(dv, window_size)

data = mx.io.NDArrayIter(
    np.array([d[0:-1] for d in dw]),
    np.array([d[-1] for d in dw]),
    label_name = 'idwr_label'
)

x = mx.sym.Variable('data')
y = mx.sym.Variable('idwr_label')

fc1 = mx.sym.FullyConnected(data = x, name = 'fc1', num_hidden = n_num)
relu1 = mx.sym.Activation(data = fc1, name = 'relu1', act_type = 'relu')

fc2 = mx.sym.FullyConnected(data = relu1, name = 'fc2', num_hidden = 1)
lro = mx.sym.LinearRegressionOutput(data = fc2, label = y, name = 'lro')

model = mx.mod.Module(
    lro,
    data_names = ['data'],
    label_names = ['idwr_label']
)

model.fit(
    data, 
    num_epoch = epoch,
    optimizer = 'adam',
    eval_metric = 'rmse',
    epoch_end_callback = lambda epch, sym, args, auxs: print("epoch = %s" % epch)
)

rmse = model.score(data, mx.metric.RMSE())

print(rmse)

res = model.predict(data)

plt.plot(dv)

plt.plot(range(window_size - 1, len(dv)), res.asnumpy())

plt.savefig(dest_file)
