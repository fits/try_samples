
import sys
from gensim.models import word2vec

data_file = sys.argv[1]
word = sys.argv[2]

sentences = word2vec.LineSentence(data_file)

model = word2vec.Word2Vec(sentences, size = 30, sg = 1, window = 2, min_count = 2, iter = 5000)


print(model[word])

print('----------')

print(model.wv.most_similar(positive = [word]))
