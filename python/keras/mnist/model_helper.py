
import json

import numpy as np
from keras.models import model_from_json
from keras.datasets import mnist
from keras.utils import np_utils

def train_mnist():
	return convert_mnist(mnist.load_data()[0])

def test_mnist():
	return convert_mnist(mnist.load_data()[1])

def load_model(file1, file2):
	model = model_from_json(load_str(file1))
	model.set_weights(load_weights(file2))
	return model

def save_model(model, file1, file2):
	save_str(model.to_json(), file1)
	save_weights(model.get_weights(), file2)

def load_weights(file):
	wg = json.loads(load_str(file))
	return [np.asarray(x, dtype = np.float32) for x in wg]

def save_weights(weights, file):
	wg = [x.tolist() for x in weights]
	save_str(json.dumps(wg), file)

def load_str(file):
	f = open(file, 'r')
	res = f.read()
	f.close()
	return res

def save_str(str, file):
	f = open(file, 'w')
	f.write(str)
	f.close()

def convert_mnist(tpl):
	features = tpl[0].reshape(tpl[0].shape[0], 1, 28, 28).astype(np.float32)
	features /= 255

	labels = np_utils.to_categorical(tpl[1], 10)

	return (features, labels)
