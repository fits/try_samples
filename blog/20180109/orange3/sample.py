
import sys
import Orange
from orangecontrib.associate.fpgrowth import *

data_file = sys.argv[1]

tbl = Orange.data.Table(data_file)

X, mapping = OneHot.encode(tbl)

itemsets = dict(frequent_itemsets(X, 5))

rules = association_rules(itemsets, 0.7)

for P, Q, support, confidence in rules:

    pitems = OneHot.decode(P, tbl, mapping)
    qitems = OneHot.decode(Q, tbl, mapping)

    lhs = list(map(lambda v: v[1].name, pitems))
    rhs = list(map(lambda v: v[1].name, qitems))

    print(f"lhs = {lhs}, rhs = {rhs}, support = {support}, confidence = {confidence}")
