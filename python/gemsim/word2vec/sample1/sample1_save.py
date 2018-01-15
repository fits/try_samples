import sys
from gensim.models import word2vec

data_file = sys.argv[1]
dest_file = sys.argv[2]

sentences = word2vec.LineSentence(data_file)

model = word2vec.Word2Vec(sentences, size = 100, sg = 1, window = 5, min_count = 2, iter = 5000)

model.save(dest_file)
