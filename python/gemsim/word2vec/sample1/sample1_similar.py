import sys
from gensim.models import word2vec

model_file = sys.argv[1]
word = sys.argv[2]

model = word2vec.Word2Vec.load(model_file)

for p, v in model.wv.most_similar(positive = [word]):
    print(f"{p}: {v}")
