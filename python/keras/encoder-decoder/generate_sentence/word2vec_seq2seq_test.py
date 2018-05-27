import sys
import numpy as np
from gensim.models import word2vec
from keras.models import load_model

model_file_prefix = sys.argv[1]
questions = [q.split(' ') for q in sys.argv[2].split(';')]

BOS = '\t'
EOS = '\n'

topn_similar = 10
prob_weight = 20
predict_max_size = 100

encode_model = load_model(f'{model_file_prefix}_encode.h5')
decode_model = load_model(f'{model_file_prefix}_decode.h5')

wv_model = word2vec.Word2Vec.load(f'{model_file_prefix}_w2v.wv')

def word_choice(v):
    cd = wv_model.wv.similar_by_vector(v, topn = topn_similar)

    words = [c for c, _ in cd]

    if EOS in words:
        return EOS

    probs = np.exp(np.array([p for _, p in cd]) * prob_weight)
    probs /= probs.sum()

    return np.random.choice(words, p = probs)

def predict(q):
    state = encode_model.predict(np.array([wv_model.wv[q]]))
    ch = BOS

    for _ in range(predict_max_size):
        r, h = decode_model.predict([np.array([wv_model.wv[[ch]]]), state])

        ch = word_choice(r[0][0])

        if ch == EOS:
            break

        yield ch
        state = h

answer = lambda q: ''.join(predict(q))

for q in questions:
    a = answer(q)
    print(f'{q} = {a}')
