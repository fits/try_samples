
from keras.models import Sequential
from keras.layers.core import Dense, Activation

from sklearn import datasets
import numpy as np

model = Sequential()

model.add(Dense(input_dim = 4, output_dim = 6))
model.add(Activation('relu'))

model.add(Dense(output_dim = 3))
model.add(Activation('softmax'))

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
