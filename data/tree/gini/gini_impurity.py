
from itertools import *

def size(xs):
	return float(len(xs))

def gini1(xs):
	return 1 - sum(map(lambda (k, g): (size(list(g)) / size(xs)) ** 2, groupby(sorted(xs))))

def countby(xs):
	return map(lambda (k, v): (k, size(list(v))), groupby(sorted(xs)))

def gini2(xs):
	return sum(map(
		lambda ((xk, xv), (yk, yv)): xv / size(xs) * yv / size(xs) * 2, 
		combinations(countby(xs), 2)
	))

vlist = ["A", "B", "B", "C", "B", "A"]

print gini1(vlist)
print gini2(vlist)
