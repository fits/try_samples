import sys
import numpy as np
from gensim.corpora import Dictionary
from keras.models import load_model

model_file_prefix = sys.argv[1]
dic_file = sys.argv[2]
questions = sys.argv[3].split(',')

BOS = '\t'
EOS = '\n'

encode_model = load_model(f'{model_file_prefix}_encode.h5')
decode_model = load_model(f'{model_file_prefix}_decode.h5')

dic = Dictionary.load(dic_file)

one_hot = lambda s: np.eye(len(dic))[dic.doc2idx(list(s))]
input_data = lambda q: np.array([one_hot(q)])

def predict(q):
    states = encode_model.predict(input_data(q))
    ch = BOS

    while True:
        r, h, c = decode_model.predict([input_data(ch)] + states)

        ch = dic[np.argmax(r)]

        if ch == EOS:
            break

        yield ch
        states = [h, c]

answer = lambda q: ''.join(predict(q))

for q in questions:
    a = answer(q)
    print(f'{q} = {a}')
