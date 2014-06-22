
from itertools import *

def size(xs):
	return float(len(xs))

# (a) 1 - (AA + BB + CC)
def giniA(xs):
	return 1 - sum(map(lambda x: (size(list(x[1])) / size(xs)) ** 2, groupby(sorted(xs))))

def countby(xs):
	return map(lambda x: (x[0], size(list(x[1]))), groupby(sorted(xs)))

# (b) AB * 2 + AC * 2 + BC * 2
def giniB(xs):
	return sum(map(
		lambda x: x[0][1] / size(xs) * x[1][1] / size(xs) * 2, 
		combinations(countby(xs), 2)
	))

vlist = ["A", "B", "B", "C", "B", "A"]

print(giniA(vlist))
print(giniB(vlist))
