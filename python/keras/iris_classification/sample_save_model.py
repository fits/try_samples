
import sys
import json

from keras.models import Sequential
from keras.layers.core import Dense, Activation

def save_str(file, data):
	f = open(file, 'w')
	f.write(data)
	f.close()

model = Sequential()

model.add(Dense(input_dim = 4, output_dim = 6))
model.add(Activation('relu'))

model.add(Dense(output_dim = 3))
model.add(Activation('softmax'))

wg = [x.tolist() for x in model.get_weights()]

save_str(sys.argv[1], model.to_json())
save_str(sys.argv[2], json.dumps(wg))
