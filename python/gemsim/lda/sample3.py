
import sys
import warnings

from collections import Counter
from statistics import mean

from gensim.corpora import Dictionary
from gensim.models.ldamodel import LdaModel
from gensim.models import word2vec

data_file = sys.argv[1]
topic_num = int(sys.argv[2])
limit_topics = 3

sentences = list(word2vec.LineSentence(data_file))

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences]

lda = LdaModel(corpus = corpus, id2word = dic, num_topics = topic_num)

doc_topics = [lda[c] for c in corpus]

avg_doc_topics = mean([len(t) for t in doc_topics])

if avg_doc_topics > limit_topics:
  warnings.warn(f'topic_num is small. topics num of doc = {avg_doc_topics}')

flatten = lambda x: sum(x, [])

topic_freq = Counter(flatten([[x[0] for x in t] for t in doc_topics]))

print('topic,freq,item,prob')

for i in range(topic_num):
  for t in lda.get_topic_terms(i):
    item = dic[t[0]]

    print(f'{i},{topic_freq[i]},{item},{t[1]}')
