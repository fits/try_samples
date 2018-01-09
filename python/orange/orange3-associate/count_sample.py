
import sys
import Orange
from orangecontrib.associate.fpgrowth import *

file = sys.argv[1]
min_support = int(sys.argv[2])

tbl = Orange.data.Table(file)

X, mapping = OneHot.encode(tbl)

itemsets = dict(frequent_itemsets(X, min_support))

cmb_itemsets = {i[0]: i[1] for i in itemsets.items() if len(i[0]) > 1}

for p, c in sorted(cmb_itemsets.items(), key = lambda x: x[1], reverse = True):
    items = OneHot.decode(p, tbl, mapping)
    cmb = list(map(lambda v: v[1].name, items))

    print(f"combination: {cmb}, count: {c}")
