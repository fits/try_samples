
import sys
import csv
import numpy as np

from functools import reduce

from sklearn import datasets
from sklearn import svm

kdiv = 150

iris_type = {"setosa": 0, "versicolor": 1, "virginica": 2}

def divide_list(xs, n):
	q = len(xs) // n
	m = len(xs) % n

	return reduce(
		lambda acc, i:
			(lambda fr = sum([ len(x) for x in acc ]):
				acc + [ xs[fr:(fr + q + (1 if i < m else 0))] ]
			)()
		,
		range(n),
		[]
	)

def exclude_list(xs, exc):
	return list(filter(lambda x: x not in exc, xs))

def dataset_tuple(items):
	return ( [ v for v in items[0:4] ], iris_type[items[4]] )

def get_data(dataset, idx):
	return [ d[idx] for d in dataset ]

def learn_and_test(train_data, test_data):
	svc = svm.SVC()

	svc.fit(get_data(train_data, 0), get_data(train_data, 1))

	prd = svc.predict(get_data(test_data, 0))

	return accuracy(prd, get_data(test_data, 1))

def accuracy(prd_data, ans_data):
	size = len(prd_data)

	r = reduce(
		lambda acc, x: acc + (1 if prd_data[x] == ans_data[x] else 0), 
		range(size), 
		0
	)

	return r / size

def cross_validation(dataset, k):
	datasize = len(dataset)

	perm = np.random.permutation(datasize)

	return [
		learn_and_test(dataset[exclude_list(perm, idx)], dataset[idx])
			for idx in divide_list(perm, k)
	]


lines = list(csv.reader(open(sys.argv[1], 'r')))

dataset = [ dataset_tuple(v) for v in lines[1:] ]

res = cross_validation(np.asarray(dataset), kdiv)

print(res.mean())
