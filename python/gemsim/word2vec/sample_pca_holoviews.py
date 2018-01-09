
import sys
import holoviews as hv
from gensim.models import word2vec
from sklearn import decomposition

data_file = sys.argv[1]
dest_file = sys.argv[2]

hv.extension('bokeh')
hv.util.output('size = 150')

sentences = word2vec.LineSentence(data_file)

model = word2vec.Word2Vec(sentences, size = 30, sg = 1, window = 2, min_count = 2, iter = 5000)

pca = decomposition.PCA(n_components = 2)

x = [model[k] for k in model.wv.index2word]

xt = pca.fit_transform(x)

ps = [
    hv.Scatter((xt[i][0], xt[i][1])) * 
    hv.Text(xt[i][0], xt[i][1], model.wv.index2word[i])
    for i in range(len(model.wv.index2word))
]

p = hv.Overlay(ps)

hv.renderer('bokeh').save(p, dest_file)
