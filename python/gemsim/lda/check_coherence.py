
import sys
import itertools
import functools
import pandas as pd
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

coh_list = ['u_mass', 'c_v', 'c_w2v', 'c_uci', 'c_npmi']

print(','.join(['topic_num'] + coh_list))

for i in range(1, max_topic_num):

    lda = LdaModel(
        corpus = corpus, 
        id2word = dic, 
        num_topics = i, 
        alpha = alpha, 
        random_state = 1
    )

    coh = [
        str(
            CoherenceModel(
                model = lda, corpus = corpus, 
                texts = sentences, coherence = c, processes = 1
            ).get_coherence()
        ) for c in coh_list
    ]

    print(','.join([str(i)] + coh))
