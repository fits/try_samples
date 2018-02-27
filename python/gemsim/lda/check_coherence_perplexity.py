
import sys
import numpy as np

from statistics import mean
from gensim.corpora import Dictionary
from gensim.models.ldamodel import LdaModel, CoherenceModel
from gensim.models import word2vec

data_file = sys.argv[1]
max_topic_num = int(sys.argv[2])
alpha = float(sys.argv[3])

sentences = [s for s in word2vec.LineSentence(data_file) if len(s) >= 2]

dic = Dictionary(sentences)

corpus = [dic.doc2bow(s) for s in sentences]

print('topic_num,avg,bound,perplexity,coherence')

for i in range(1, max_topic_num + 1):

    lda = LdaModel(
        corpus = corpus, 
        id2word = dic, 
        num_topics = i, 
        alpha = alpha, 
        random_state = 1
    )

    avg_topics = mean([len(t) for t in [lda[c] for c in corpus]])

    bound = lda.bound(corpus)

    perwordbound = lda.log_perplexity(corpus)
    perplexity = np.exp2(-perwordbound)

    cm = CoherenceModel(model = lda, corpus = corpus, coherence = 'u_mass', processes = 1)
    coherence = cm.get_coherence()

    print(f"{i},{avg_topics},{bound},{perplexity},{coherence}")
