
from itertools import groupby

vlist = ["A", "B", "B", "C", "B", "A"]

for k, g in groupby(sorted(vlist)):
	print "key = %s, count = %d" % (k, len(list(g)))
