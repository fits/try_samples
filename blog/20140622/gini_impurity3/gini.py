
from itertools import *

def size(xs):
	return float(len(xs))

# (a) 1 - (AA + BB + CC)
def giniA(xs):
	return 1 - sum(map(lambda (k, g): (size(list(g)) / size(xs)) ** 2, groupby(sorted(xs))))

def countby(xs):
	return map(lambda (k, v): (k, size(list(v))), groupby(sorted(xs)))

# (b) AB * 2 + AC * 2 + BC * 2
def giniB(xs):
	return sum(map(
		lambda ((xk, xv), (yk, yv)): xv / size(xs) * yv / size(xs) * 2, 
		combinations(countby(xs), 2)
	))

vlist = ["A", "B", "B", "C", "B", "A"]

print giniA(vlist)
print giniB(vlist)
