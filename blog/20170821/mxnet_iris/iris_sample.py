
import mxnet as mx
import numpy as np

train_test_rate = 0.7

def categorical(ds):
	ct = np.unique(ds).tolist()
	return np.vectorize(lambda x: ct.index(x))(ds)

iris = np.loadtxt('iris.data', delimiter = ',', dtype = [
	('sepal-length', 'f4'), ('sepal-width', 'f4'), 
	('petal-length', 'f4'), ('petal-width', 'f4'), 
	('species', 'S15')
])

iris_data = np.c_[
	iris['sepal-length'], 
	iris['sepal-width'], 
	iris['petal-length'], 
	iris['petal-width']
]

iris_label = categorical(iris['species'])


data_size = len(iris)
train_size = int(data_size * train_test_rate)

perm = np.random.permutation(data_size)
train_perm = perm[0:train_size]
test_perm = perm[train_size:]

train_iter = mx.io.NDArrayIter(iris_data[train_perm], iris_label[train_perm])
test_iter = mx.io.NDArrayIter(iris_data[test_perm], iris_label[test_perm])

data = mx.sym.Variable('data')

net = mx.sym.FullyConnected(data = data, name = 'fc1', num_hidden = 5)
net = mx.sym.Activation(data = net, name = 'relu1', act_type = 'relu')

net = mx.sym.FullyConnected(data = net, name = 'fc2', num_hidden = 3)
net = mx.sym.SoftmaxOutput(data = net, name = 'softmax')

mod = mx.mod.Module(net)

mod.fit(train_iter, num_epoch = 20)

res = mod.score(test_iter, mx.metric.Accuracy())

print(res)

print('----------')

for k, v in mod.get_params()[0].items():
	print('%s:' % k)
	print(v.asnumpy())
	print('----------')
