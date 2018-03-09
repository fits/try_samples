
import sys

from gensim.corpora import Dictionary
from gensim.matutils import corpus2dense
from gensim.models import word2vec

data_file = sys.argv[1]

sentences = word2vec.LineSentence(data_file)

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences]

ds = corpus2dense(corpus, len(dic))

print(len(ds))
print(ds)

print('-----')

print(len(ds.T))
print(ds.T)
