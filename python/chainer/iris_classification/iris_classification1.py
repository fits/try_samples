
import chainer
import chainer.functions as F
import chainer.optimizers
import numpy as np
from sklearn import datasets

iterations = 10000
train_size_rate = 0.9
loss_rate = 0.1

iris = datasets.load_iris()
iris.data = iris.data.astype(np.float32)
iris.target = iris.target.astype(np.int32)

model = chainer.FunctionSet(
	l1 = F.Linear(4, 3),
)

optimizer = chainer.optimizers.Adam()
optimizer.setup(model)

def forward(x, t):
	vx = chainer.Variable(x)
	vt = chainer.Variable(t)

	h = model.l1(vx)
	return F.softmax_cross_entropy(h, vt), F.accuracy(h, vt)


data_size = len(iris.data)
train_size = int(data_size * train_size_rate)

train_res = []
test_res = []

for n in range(iterations):
	perm = np.random.permutation(data_size)

	optimizer.zero_grads()

	e, a = forward(
		iris.data[ perm[:train_size] ],
		iris.target[ perm[:train_size] ]
	)

	e.backward()

	optimizer.weight_decay(0.001)
	optimizer.update()

	train_res.append( (e.data, a.data) )

	te, ta = forward(
		iris.data[ perm[train_size:] ],
		iris.target[ perm[train_size:] ]
	)

	test_res.append( (te.data, ta.data) )

	if e.data <= loss_rate:
		break


print('iterations: ', len(train_res))
print('accuracy: ', sum(x[1] for x in test_res) / len(test_res))
