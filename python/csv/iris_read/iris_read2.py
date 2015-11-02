
import sys
import csv
import random

iris_type = {"setosa": 0, "versicolor": 1, "virginica": 2}

def features(items):
	return [ float(v) for v in items[0:4] ]

lines = list(csv.reader(open(sys.argv[1], 'r')))

values = lines[1:]

d = [( features(v), iris_type[v[4]] ) for v in values]

print(d)