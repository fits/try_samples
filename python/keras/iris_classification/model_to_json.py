
import sys

from keras.models import Sequential
from keras.layers.core import Dense, Activation

model = Sequential()

model.add(Dense(input_dim = 4, output_dim = 6))
model.add(Activation('relu'))

model.add(Dense(output_dim = 3))
model.add(Activation('softmax'))

f = open(sys.argv[1], 'w')

f.write(model.to_json())

f.close()
