
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

def save_str(file, data):
	f = open(file, 'w')
	f.write(data)
	f.close()

model = model_from_json(load_str(sys.argv[1]))

wgList = json.loads(load_str(sys.argv[2]))
wg = [np.asarray(x, dtype = np.float32) for x in wgList]

model.set_weights(wg)

model.compile(loss = 'categorical_crossentropy', optimizer = 'adam', metrics = ['accuracy'])

(x_train, y_train), (x_test, y_test) = mnist.load_data()

x_train = x_train.reshape(x_train.shape[0], 1, 28, 28).astype('float32')
x_train /= 255

y_train = np_utils.to_categorical(y_train, 10)

model.fit(x_train, y_train, nb_epoch = 5, batch_size = 20)

wg2 = [x.tolist() for x in model.get_weights()]

save_str(sys.argv[3], json.dumps(wg2))
