
import sys

from statistics import mean

from gensim.corpora import Dictionary
from gensim.models.ldaseqmodel import LdaSeqModel
from gensim.models import word2vec

data_file = sys.argv[1]
topic_num = int(sys.argv[2])

sentences = list(word2vec.LineSentence(data_file))

dic = Dictionary(sentences)

corpus = [ dic.doc2bow(s) for s in sentences if len(s) >= 2 ]

ldaseq = LdaSeqModel(corpus = corpus, id2word = dic, num_topics = topic_num, time_slice = [len(corpus)])

for i, ts in enumerate(ldaseq.print_topics(top_terms = 10)):
  for t in ts:
    print(f'{i},{t[0]},{t[1]}')


select_topics = lambda ts: [ (i, p) for i, p in enumerate(ts) if p >= 0.01 ]

doc_topics = [ (i, select_topics(ldaseq.doc_topics(i))) for i in range(len(corpus)) ]

print('--------------------')

print( mean([len(d[1]) for d in doc_topics]) )

print('--------------------')

for d in doc_topics:
  items = [ dic[c[0]] for c in corpus[d[0]] ]

  print(f"{d[0]}, items={items}, topics={d[1]}")
