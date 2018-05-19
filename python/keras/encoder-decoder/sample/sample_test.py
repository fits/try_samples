import sys
import numpy as np
from gensim.corpora import Dictionary
from keras.models import load_model

model_file_prefix = sys.argv[1]
dic_file = sys.argv[2]
input_size = int(sys.argv[3])
questions = sys.argv[4].split(',')

BOS = '\t'
EOS = '\n'

encode_model = load_model(f'{model_file_prefix}_encode.h5')
decode_model = load_model(f'{model_file_prefix}_decode.h5')

dic = Dictionary.load(dic_file)

padding_one_hot = lambda d: np.vstack((
    np.eye(len(dic))[dic.doc2idx(list(d))],
    np.zeros((input_size - len(d), len(dic)))
))

one_hot = lambda s: np.eye(len(dic))[dic.doc2idx(list(s))]
decoder_input = lambda q: np.array([one_hot(q)])

def predict(q):
    state = encode_model.predict(np.array([padding_one_hot(q)]))
    ch = BOS

    while True:
        r, h = decode_model.predict([decoder_input(ch), state])

        ch = dic[np.argmax(r)]

        if ch == EOS:
            break

        yield ch
        state = h

answer = lambda q: ''.join(predict(q))

for q in questions:
    a = answer(q)
    print(f'{q} = {a}')
