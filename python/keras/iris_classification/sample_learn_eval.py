
import sys
import json

import numpy as np
from keras.models import model_from_json
from sklearn import datasets

def load_str(file):
	f = open(file, 'r')

	res = f.read()

	f.close()

	return res

model = model_from_json(load_str(sys.argv[1]))

wgList = json.loads(load_str(sys.argv[2]))

wg = [np.asarray(x, dtype = np.float32) for x in wgList]

model.set_weights(wg)

model.compile(loss = 'sparse_categorical_crossentropy', optimizer = 'sgd', metrics = ['accuracy'])

iris = datasets.load_iris()

data_size = len(iris.data)
train_size = int(data_size * 0.8)

perm = np.random.permutation(data_size)

x_train = iris.data[ perm[0:train_size] ]
y_train = iris.target[ perm[0:train_size] ]

model.fit(x_train, y_train, nb_epoch = 50, batch_size = 1)

print('-----')

x_test = iris.data[ perm[train_size:] ]
y_test = iris.target[ perm[train_size:] ]

res = model.evaluate(x_test, y_test, batch_size = 1)

print(res)
