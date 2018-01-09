
import sys
import Orange
from orangecontrib.associate.fpgrowth import *

data_file = sys.argv[1]

tbl = Orange.data.Table(data_file)

X, mapping = OneHot.encode(tbl)

itemsets = dict(frequent_itemsets(X, 5))

rules = association_rules(itemsets, 0.7)

stats = rules_stats(rules, itemsets, len(X))

def decode_onehot(d):
    items = OneHot.decode(d, tbl, mapping)
    return list(map(lambda v: v[1].name, items))

for s in sorted(stats, key = lambda x: x[6], reverse = True):

    lhs = decode_onehot(s[0])
    rhs = decode_onehot(s[1])

    support = s[2]
    confidence = s[3]
    lift = s[6]

    print(f"lhs = {lhs}, rhs = {rhs}, support = {support}, confidence = {confidence}, lift = {lift}")
