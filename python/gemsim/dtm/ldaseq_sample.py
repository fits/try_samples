
import sys

from gensim.corpora import Dictionary
from gensim.models.ldaseqmodel import LdaSeqModel
from gensim.models import word2vec

data_file = sys.argv[1]
topic_num = int(sys.argv[2])

sentences = list(word2vec.LineSentence(data_file))

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences]

ldaseq = LdaSeqModel(corpus = corpus, id2word = dic, num_topics = topic_num, time_slice = [len(corpus)])

print('topic,item,prob')

for i, ts in enumerate(ldaseq.print_topics(top_terms = 10)):
  for t in ts:
    print(f'{i},{t[0]},{t[1]}')
