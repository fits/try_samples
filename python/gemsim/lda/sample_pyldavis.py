
import sys
import pyLDAvis.gensim

from statistics import mean

from gensim.corpora import Dictionary
from gensim.models.ldamodel import LdaModel
from gensim.models import word2vec

data_file = sys.argv[1]
topic_num = int(sys.argv[2])
dest_file = sys.argv[3]

sentences = list(word2vec.LineSentence(data_file))

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences if len(s) >= 2]

lda = LdaModel(corpus = corpus, id2word = dic, num_topics = topic_num, random_state = 1)

doc_topics = [lda[c] for c in corpus]

avg_doc_topics = mean([len(t) for t in doc_topics])

print(f"topics num of doc = {avg_doc_topics}")

vis = pyLDAvis.gensim.prepare(lda, corpus, dic, n_jobs = 1, mds='mmds', sort_topics = False)

pyLDAvis.save_html(vis, dest_file)
