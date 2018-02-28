
import sys
import numpy as np
import matplotlib.pyplot as plt

from toolz import first, second, compose, partial
from gensim.corpora import Dictionary
from gensim.models import word2vec
from sklearn import decomposition

data_file = sys.argv[1]
dest_img = sys.argv[2]

sentences = [s for s in word2vec.LineSentence(data_file) if len(s) >= 2]

dic = Dictionary(sentences)

x = np.zeros((len(sentences), len(dic)))

for i, d in enumerate(sentences):
    x[np.ix_([i], dic.doc2idx(d))] = 1

pca = decomposition.PCA(n_components = 2, random_state = 1)

nx = pca.fit_transform(x)

print(sum(pca.explained_variance_ratio_))

fst = compose(list, partial(map, first))
snd = compose(list, partial(map, second))

plt.scatter(fst(nx), snd(nx))

plt.xlabel('PC1')
plt.ylabel('PC2')

plt.savefig(dest_img)
