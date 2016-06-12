
from sklearn.datasets import fetch_mldata

mnist = fetch_mldata('MNIST original', data_home = '/work')

print(mnist.data.shape)

print(mnist.data[0])
print(mnist.target[0])
