
import sys
import pandas as pd

from collections import Counter
from gensim.corpora import Dictionary
from gensim.models.ldamodel import LdaModel

topic_num = int(sys.argv[1])

df = pd.read_csv('data/sample2.csv')

dfg = df.groupby('transaction')['item'].apply(tuple)

sentences = [[w for w in t] for t in dfg.tolist()]

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences]

lda = LdaModel(corpus = corpus, id2word = dic, num_topics = topic_num)

doc_topics = [lda[c] for c in corpus]

flatten = lambda x: sum(x, [])

topic_freq = Counter(flatten([[x[0] for x in t] for t in doc_topics]))

print('topic,freq,item,prob')

for i in range(topic_num):
  for t in lda.get_topic_terms(i):
    item = dic[t[0]]

    print(f'{i},{topic_freq[i]},{item},{t[1]}')
