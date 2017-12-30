
import sys
import matplotlib.pyplot as plt
from gensim.models import word2vec
from sklearn import decomposition

data_file = sys.argv[1]
dest_img = sys.argv[2]

sentences = word2vec.LineSentence(data_file)

model = word2vec.Word2Vec(sentences, size = 30, sg = 1, window = 2, min_count = 2, iter = 5000)

pca = decomposition.PCA(n_components = 2)

x = [model[k] for k in model.wv.index2word]

xt = pca.fit_transform(x)

print(xt)

for i in range(len(model.wv.index2word)):
    plt.scatter(xt[i][0], xt[i][1])
    plt.annotate(model.wv.index2word[i], (xt[i][0], xt[i][1]))

plt.savefig(dest_img)
