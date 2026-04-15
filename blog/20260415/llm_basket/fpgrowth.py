from mlxtend.preprocessing import TransactionEncoder
from mlxtend.frequent_patterns import fpgrowth, association_rules

import pandas as pd
import sys

data_file = sys.argv[1]
min_support = float(sys.argv[2])
min_threshold = float(sys.argv[3])

with open(data_file) as f:
    dataset = [line.strip().split(',') for line in f]

te = TransactionEncoder()

te_a = te.fit(dataset).transform(dataset)

df = pd.DataFrame(te_a, columns=te.columns_)

freq = fpgrowth(df, min_support=min_support, use_colnames=True)

rules = association_rules(freq, metric='lift', min_threshold=min_threshold)

print( rules[['antecedents', 'consequents', 'lift', 'confidence', 'support']] )
