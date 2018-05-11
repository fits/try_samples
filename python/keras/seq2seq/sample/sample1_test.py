import sys
import numpy as np
from gensim.corpora import Dictionary
from keras.models import load_model

model_file = sys.argv[1]
dic_file = sys.argv[2]
questions = sys.argv[3].split(',')

model = load_model(model_file)

dic = Dictionary.load_from_text(dic_file)

one_hot = lambda s: np.eye(len(dic))[dic.doc2idx(list(s))]

x = np.array([one_hot(q.ljust(model.input_shape[1], ' ')) for q in questions])

rs = model.predict_classes(x)

to_str = lambda ds: ''.join([dic[d] for d in ds])

answers = [to_str(r) for r in rs]

for q, a in zip(questions, answers):
    print(f'{q} = {a}')
