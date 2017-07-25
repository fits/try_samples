
import mxnet as mx
import numpy as np

iris = np.loadtxt('iris.data', delimiter = ',', dtype = [
	('sl', 'f4'), ('sw', 'f4'), ('pl', 'f4'), ('pw', 'f4'), ('kn', 'S15')
])

categories = np.unique(iris['kn']).tolist()
factor = np.vectorize(lambda x: categories.index(x))

iris_data = np.c_[iris['sl'], iris['sw'], iris['pl'], iris['pw']]
iris_label = factor(iris['kn'])

iter = mx.io.NDArrayIter(iris_data, iris_label, shuffle = True)

data = mx.sym.Variable('data')

net = mx.sym.FullyConnected(data = data, name = 'fc1', num_hidden = 5)
net = mx.sym.Activation(data = net, name = 'relu1', act_type = 'relu')

net = mx.sym.FullyConnected(data = net, name = 'fc2', num_hidden = 3)
net = mx.sym.SoftmaxOutput(data = net, name = 'softmax')

mod = mx.mod.Module(net)

mod.fit(iter, num_epoch = 20)

res = mod.score(iter, mx.metric.Accuracy())

print(res)
