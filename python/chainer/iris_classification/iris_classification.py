
import sys
import csv
import chainer
import chainer.functions as F
import chainer.optimizers
import numpy as np

batch = 10
max_epoch = 1000
loss_rate = 0.1
hidden_layer_num = 5

iris_type = {"setosa": 0, "versicolor": 1, "virginica": 2}

def dataset_tuple(items):
	return ( [ v for v in items[0:4] ], iris_type[items[4]] )

lines = list(csv.reader(open(sys.argv[1], 'r')))

dataset = [ dataset_tuple(v) for v in lines[1:] ]

model = chainer.FunctionSet(
	l1 = F.Linear(4, hidden_layer_num),
	l2 = F.Linear(hidden_layer_num, 3),
)

optimizer = chainer.optimizers.Adam()
optimizer.setup(model)

def forward(x, t):
	u2 = model.l1(x)
	z2 = F.sigmoid(u2)
	#z2 = F.relu(u2)

	h = model.l2(z2)

	return F.softmax_cross_entropy(h, t), F.accuracy(h, t)

def to_variable(ds, typ):
	return chainer.Variable(np.asarray(ds, dtype = typ))

def to_variable_data(ds):
	return (
		to_variable([ d[0] for d in ds ], np.float32),
		to_variable([ d[1] for d in ds ], np.int32),
	)

def learn(dataset, batch_size = 10, epoch = 1000, train_size_rate = 0.9):
	data_size = len(dataset)

	train_res = []
	test_res = []

	train_size = int(data_size * train_size_rate)

	for n in range(epoch):
		perm = np.random.permutation(data_size)

		for i in range(0, train_size, batch_size):
			optimizer.zero_grads()

			x, t = to_variable_data( dataset[perm[i : i + batch_size]] )

			e, a = forward(x, t)

			e.backward()

			optimizer.weight_decay(0.001)
			optimizer.update()

			train_res.append( (e.data, a.data) )

		tx, tt = to_variable_data( dataset[perm[train_size:]] )

		te, ta = forward(tx, tt)

		test_res.append( (te.data, ta.data) )

		if any(x[0] <= loss_rate for x in train_res[-batch_size:]):
			break

	return train_res, test_res

train_res, test_res = learn(np.asarray(dataset), batch, max_epoch)

print(train_res[-3:])
print(test_res[-5:])
print()

print('times: ', len(train_res))
print('accuracy: ', sum(x[1] for x in test_res) / len(test_res))
