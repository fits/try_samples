
import sys

from keras.models import model_from_json

from sklearn import datasets
import numpy as np

f = open(sys.argv[1], 'r')

json = f.read()

f.close()

model = model_from_json(json)

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
