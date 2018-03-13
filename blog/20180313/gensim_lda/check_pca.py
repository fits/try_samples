
import sys

from gensim.corpora import Dictionary
from gensim.matutils import corpus2dense
from gensim.models import word2vec
from sklearn import decomposition

data_file = sys.argv[1]
pca_num = int(sys.argv[2])

sentences = [s for s in word2vec.LineSentence(data_file) if len(s) >= 2]

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences]

x = corpus2dense(corpus, len(dic)).T

pca = decomposition.PCA(n_components = pca_num, random_state = 1)

nx = pca.fit_transform(x)

print(sum(pca.explained_variance_ratio_))
