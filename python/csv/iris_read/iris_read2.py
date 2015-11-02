
import sys
import csv

iris_type = {"setosa": 0, "versicolor": 1, "virginica": 2}

def features(items):
	return [ float(v) for v in items[0:4] ]

lines = list(csv.reader(open(sys.argv[1], 'r')))

values = lines[1:]

dlist = [( features(v), iris_type[v[4]] ) for v in values]

print(dlist)

print('-----')

print([d[0] for d in dlist])
print([d[1] for d in dlist])
