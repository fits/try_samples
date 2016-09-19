
import numpy as np
from keras.datasets import mnist
from keras.utils import np_utils

def train_mnist():
	return convert_mnist(mnist.load_data()[0])

def test_mnist():
	return convert_mnist(mnist.load_data()[1])

def convert_mnist(tpl):
	features = tpl[0].reshape(tpl[0].shape[0], 1, 28, 28).astype(np.float32)
	features /= 255

	labels = np_utils.to_categorical(tpl[1], 10)

	return (features, labels)
