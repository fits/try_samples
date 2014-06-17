
from itertools import *

vlist = ["A", "B", "B", "C", "B", "A"]

for ((xk, xv), (yk, yv)) in combinations(groupby(sorted(vlist)), 2):
	print "%s - %s" % (xk, yk)
