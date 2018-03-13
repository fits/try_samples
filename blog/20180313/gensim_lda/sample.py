
import sys
import pyLDAvis.gensim

from statistics import mean
from toolz import frequencies

from gensim.corpora import Dictionary
from gensim.models.ldamodel import LdaModel
from gensim.models import word2vec

data_file = sys.argv[1]
topic_num = int(sys.argv[2])
alpha = float(sys.argv[3])
dest_file = sys.argv[4]

sentences = [s for s in word2vec.LineSentence(data_file) if len(s) >= 2]

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences]

lda = LdaModel(corpus = corpus, id2word = dic, num_topics = topic_num, alpha = alpha, random_state = 1)

doc_topics = [lda[c] for c in corpus]

avg_doc_topics = mean([len(t) for t in doc_topics])

print(f"topics num of doc = {avg_doc_topics}")

topic_freq = frequencies([t[0] for dt in doc_topics for t in dt])

print('----------')

for i in range(topic_num):
  items = [(dic[t[0]], t[1]) for t in lda.get_topic_terms(i, topn = 5)]
  freq = topic_freq[i] if i in topic_freq else 0
  
  print(f"topic_id = {i}, freq = {freq}, items = {items}")

print('----------')

for i in range(len(corpus)):
  dts = lda.get_document_topics(corpus[i], per_word_topics = True)

  for dt in dts[2]:
    item = dic[dt[0]]
    print(f"corpus = {i}, item = {item}, topic_id = {dt[1]}")

vis = pyLDAvis.gensim.prepare(lda, corpus, dic, n_jobs = 1, sort_topics = False)

pyLDAvis.save_html(vis, dest_file)
