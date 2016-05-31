
from keras.models import Sequential
from keras.layers.core import Dense, Activation
from sklearn import datasets

model = Sequential()

model.add(Dense(input_dim = 4, output_dim = 8))
model.add(Activation('relu'))

model.add(Dense(output_dim = 3))
model.add(Activation('softmax'))

model.compile(loss = 'sparse_categorical_crossentropy', optimizer = 'sgd', metrics = ['accuracy'])

iris = datasets.load_iris()

model.fit(iris.data, iris.target, nb_epoch = 50, batch_size = 1)
