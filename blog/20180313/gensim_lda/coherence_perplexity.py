
import sys
import numpy as np

from gensim.corpora import Dictionary
from gensim.models.ldamodel import LdaModel, CoherenceModel
from gensim.models import word2vec

data_file = sys.argv[1]
alpha = float(sys.argv[2])

sentences = [s for s in word2vec.LineSentence(data_file) if len(s) >= 2]

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences]

for i in range(1, 31):
  lda = LdaModel(corpus = corpus, id2word = dic, num_topics = i, alpha = 0.01, random_state = 1)

  cm = CoherenceModel(model = lda, corpus = corpus, coherence = 'u_mass')
  coherence = cm.get_coherence()

  perwordbound = lda.log_perplexity(corpus)
  perplexity = np.exp2(-perwordbound)

  print(f"num_topics = {i}, coherence = {coherence}, perplexity = {perplexity}")