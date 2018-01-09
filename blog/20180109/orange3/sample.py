
import sys
import Orange
from orangecontrib.associate.fpgrowth import *

data_file = sys.argv[1]

tbl = Orange.data.Table(data_file)

X, mapping = OneHot.encode(tbl)

itemsets = dict(frequent_itemsets(X, 5))

rules = association_rules(itemsets, 0.7)

def decode_onehot(d):
    items = OneHot.decode(d, tbl, mapping)
    return list(map(lambda v: v[1].name, items))

for P, Q, support, confidence in rules:
    lhs = decode_onehot(P)
    rhs = decode_onehot(Q)

    print(f"lhs = {lhs}, rhs = {rhs}, support = {support}, confidence = {confidence}")
