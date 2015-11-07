
import sys
import csv
import chainer
import chainer.functions as F
import chainer.optimizers
import numpy as np

from functools import reduce

pnum = 5
max_epoch = 10000
loss_base = 0.1
kdiv = 10
optimizer = chainer.optimizers.Adam()

iris_type = {"setosa": 0, "versicolor": 1, "virginica": 2}

def divide_list(xs, n):
	q = len(xs) // n
	m = len(xs) % n

	n = min(n, len(xs))

	return reduce(
		lambda acc, i:
			(lambda fr = sum([ len(x) for x in acc ]):
				acc + [ xs[fr:(fr + q + (1 if i < m else 0))] ]
			)()
		,
		range(n),
		[]
	)

def exclude_list(xs, exc):
	return list(filter(lambda x: x not in exc, xs))

def dataset_tuple(items):
	return ( [ v for v in items[0:4] ], iris_type[items[4]] )

def create_model():
	model = chainer.FunctionSet(
		l1 = F.Linear(4, pnum),
		l2 = F.Linear(pnum, 3),
	)

	return model

def forward(model, x):
	u2 = model.l1(x)
	#z2 = F.sigmoid(u2)
	z2 = F.relu(u2)
	return model.l2(z2)

def loss(h, t):
	return F.softmax_cross_entropy(h, t)

def to_variable(ds, typ):
	return chainer.Variable(np.asarray(ds, dtype = typ))

def learn(model, opt, ds):
	x = to_variable([ d[0] for d in ds ], np.float32)
	t = to_variable([ d[1] for d in ds ], np.int32)

	opt.setup(model)

	for i in range(max_epoch):
		opt.zero_grads()

		h = forward(model, x)
		e = loss(h, t)
		a = F.accuracy(h, t)

		e.backward()

		opt.weight_decay(0.001)
		opt.update()

		if e.data < loss_base:
			print("iterate: ", i, ", loss: ", e.data)
			break

def test(model, ds):
	x = to_variable([ d[0] for d in ds ], np.float32)
	t = to_variable([ d[1] for d in ds ], np.int32)

	h = forward(model, x)
	e = loss(h, t)

	a = F.accuracy(h, t)
	return (e.data, a.data)

def learn_and_test(opt, train_data, test_data):
	model = create_model()

	learn(model, opt, train_data)

	return test(model, test_data)

def cross_validation(dataset, opt, k):
	datasize = len(dataset)

	perm = np.random.permutation(datasize)

	return [
		learn_and_test(opt, dataset[exclude_list(perm, idx)], dataset[idx])
			for idx in divide_list(perm, k)
	]

lines = list(csv.reader(open(sys.argv[1], 'r')))

dataset = [ dataset_tuple(v) for v in lines[1:] ]

res = cross_validation(np.asarray(dataset), optimizer, kdiv)

acc = sum([ r[1] for r in res ]) / len(res)

print(acc)
