
import sys

from gensim.corpora import Dictionary
from gensim.models.ldamodel import LdaModel
from gensim.models import word2vec

data_file = sys.argv[1]
topic_num = int(sys.argv[2])
alpha = float(sys.argv[3])

sentences = [s for s in word2vec.LineSentence(data_file) if len(s) >= 2]

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences]

lda = LdaModel(corpus = corpus, num_topics = topic_num, id2word = dic, alpha = alpha, random_state = 1)

for t in lda.top_topics(corpus = corpus):
    print(f"coherence = {t[1]}, topic = {t[0]}")
