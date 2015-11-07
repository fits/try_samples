
import sys
import csv
import chainer
import chainer.functions as F
import chainer.optimizers
import numpy as np

batch = 10
max_epoch = 10000
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

def learn(dataset, batchsize = 10, epoch = 1000):
	datasize = len(dataset)

	train_res = []

	for n in range(epoch):
		perm = np.random.permutation(datasize)

		for i in range(0, datasize, batchsize):
			ds = dataset[perm[i : i + batchsize]]

			x = to_variable([ d[0] for d in ds ], np.float32)
			t = to_variable([ d[1] for d in ds ], np.int32)

			optimizer.zero_grads()

			e, a = forward(x, t)

			e.backward()
			optimizer.update()

			train_res.append( (e.data, a.data) )

		if any(x[0] <= loss_rate for x in train_res[-batchsize:]):
			break

	return train_res

train_res = learn(np.asarray(dataset), batch, max_epoch)

print(train_res[-3:])
print('times: ', len(train_res))
