
import sys
import csv
import chainer
import chainer.functions as F
import chainer.optimizers
import numpy as np

num = 5

iris_type = {"setosa": 0, "versicolor": 1, "virginica": 2}

def dataset_tuple(items):
	return ( [ v for v in items[0:4] ], iris_type[items[4]] )

lines = list(csv.reader(open(sys.argv[1], 'r')))

dataset = [ dataset_tuple(v) for v in lines[1:] ]

model = chainer.FunctionSet(
	l1 = F.Linear(4, num),
	l2 = F.Linear(num, 3),
)

optimizer = chainer.optimizers.Adam()
#optimizer = chainer.optimizers.SGD()
optimizer.setup(model.parameters)

def forward(x):
	u2 = model.l1(x)
	#z2 = F.sigmoid(u2)
	z2 = F.relu(u2)
	u3 = model.l2(z2)
	return F.sigmoid(u3)

def loss(h, t):
	return F.softmax_cross_entropy(h, t)

def to_variable(ds, typ):
	return chainer.Variable(np.asarray(ds, dtype = typ))

def learn(dataset, batchsize = 10, times = 1):
	datasize = len(dataset)

	for n in range(times):
		perm = np.random.permutation(datasize)

		for i in range(0, datasize, batchsize):
			ds = dataset[perm[i : i + batchsize]]

			x = to_variable([ d[0] for d in ds ], np.float32)
			t = to_variable([ d[1] for d in ds ], np.int32)

			optimizer.zero_grads()

			h = forward(x)
			e = loss(h, t)

			a = F.accuracy(h, t)

			print(e.data)
			print(a.data)

			e.backward()
			optimizer.update()

learn(np.asarray(dataset), 30, 5)

print(model.parameters)
