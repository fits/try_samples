
import sys
import numpy as np
from gensim.models.word2vec import Word2Vec
from keras.models import load_model

file_prefix = sys.argv[1]
num = int(sys.argv[2])

gen = load_model(f'{file_prefix}.gen')
wv_model = Word2Vec.load(f'{file_prefix}.wv')

def answer():
    x = np.random.normal(0, 1, (1,) + gen.input_shape[1:])

    y = gen.predict(x)

    res = [wv_model.similar_by_vector(w)[0][0] for w in y[0]]

    return ''.join(res)

for _ in range(num):
    print( answer() )
    print('-----')
