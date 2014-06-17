
from itertools import groupby

def size(xs):
	return float(len(xs))

def gini1(xs):
	return 1 - sum(map(lambda (k, g): (size(list(g)) / size(xs)) ** 2, groupby(sorted(xs))))

vlist = ["A", "B", "B", "C", "B", "A"]

print gini1(vlist)
