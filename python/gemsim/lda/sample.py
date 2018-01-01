
import sys
from gensim.corpora import lowcorpus
from gensim.models import ldamodel

data_file = sys.argv[1]

corpus = lowcorpus.LowCorpus(data_file)

model = ldamodel.LdaModel(corpus, num_topics = 5, id2word = corpus.id2word)

for t in model.show_topics():
    print(t)
