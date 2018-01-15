import sys
from gensim.models import word2vec
from sklearn import decomposition

model_file = sys.argv[1]

model = word2vec.Word2Vec.load(model_file)

pca = decomposition.PCA(n_components = 2)

x = [model[k] for k in model.wv.index2word]

xt = pca.fit_transform(x)

print('word,x,y')

for i in range(len(model.wv.index2word)):
    print(f"{model.wv.index2word[i]},{xt[i][0]},{xt[i][1]}")
