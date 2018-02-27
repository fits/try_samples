
import sys
import numpy as np

from gensim.corpora import Dictionary
from gensim.models import word2vec
from sklearn import decomposition

data_file = sys.argv[1]
pca_num = int(sys.argv[2])

limit_value = 0.1

sentences = [s for s in word2vec.LineSentence(data_file) if len(s) >= 2]

dic = Dictionary(sentences)

x = np.zeros((len(sentences), len(dic)))

for i, d in enumerate(sentences):
    x[np.ix_([i], dic.doc2idx(d))] = 1

pca = decomposition.PCA(n_components = pca_num, random_state = 1)

nx = pca.fit_transform(x)

print(sum(pca.explained_variance_ratio_))

for i, pc in enumerate(pca.components_):

    ids = np.where((pc >= limit_value) | (pc <= -limit_value))
    items = [dic[id] for id in ids[0]]

    for r in sorted(zip(items, pc[ids]), key = lambda x: -x[1]):
        print(f"pc = {i}, item = {r[0]}, value = {r[1]}")
