import Orange
from orangecontrib.associate.fpgrowth import *

tbl = Orange.data.Table('data/sample.basket')

X, mapping = OneHot.encode(tbl)

itemsets = dict(frequent_itemsets(X, 5))

for P, Q, supp, conf in association_rules(itemsets, 0.6):
    pitems = OneHot.decode(P, tbl, mapping)
    qitems = OneHot.decode(Q, tbl, mapping)

    lhs = list(map(lambda v: v[1].name, pitems))
    rhs = list(map(lambda v: v[1].name, qitems))

    print(f"lhs = {lhs}, rhs = {rhs}, support = {supp}, confidence = {conf}")
