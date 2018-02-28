
import sys
import itertools
import toolz

from gensim.models import word2vec

data_file = sys.argv[1]

sentences = [
    s for s in word2vec.LineSentence(data_file)
    if toolz.count(toolz.unique(s)) >= 2
]

cmb = toolz.frequencies(
    toolz.mapcat(
        lambda s: itertools.combinations(sorted(toolz.unique(s)), 2), 
        sentences
    )
)

for (k1, k2), v in cmb.items():
    print(f"item1 = {k1}, item2 = {k2}, freq = {v}")
