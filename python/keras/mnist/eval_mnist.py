
import sys
import json

import numpy as np
from keras.models import model_from_json
from keras.datasets import mnist
from keras.utils import np_utils

def load_str(file):
	f = open(file, 'r')
	res = f.read()
	f.close()
	return res

model = model_from_json(load_str(sys.argv[1]))

wgList = json.loads(load_str(sys.argv[2]))
wg = [np.asarray(x, dtype = np.float32) for x in wgList]

model.set_weights(wg)

model.compile(loss = 'categorical_crossentropy', optimizer = 'adam', metrics = ['accuracy'])

tr, (x_test, y_test) = mnist.load_data()

x_test = x_test.reshape(x_test.shape[0], 1, 28, 28).astype('float32')
x_test /= 255

y_test = np_utils.to_categorical(y_test, 10)

(loss, acc) = model.evaluate(x_test, y_test, verbose = 0)

print(loss)
print(acc)
