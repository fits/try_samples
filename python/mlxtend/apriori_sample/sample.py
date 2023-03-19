from mlxtend.preprocessing import TransactionEncoder
from mlxtend.frequent_patterns import apriori

import pandas as pd

with open('data/sample.basket') as f:
    dataset = [line.strip().split(',') for line in f]

te = TransactionEncoder()

te_a = te.fit(dataset).transform(dataset)

df = pd.DataFrame(te_a, columns=te.columns_)

freq = apriori(df, min_support=0.05, use_colnames=True)
freq['len'] = freq['itemsets'].apply(len)

r = freq[freq['len'] > 1].sort_values('support', ascending=False)

print( r.to_markdown() )
