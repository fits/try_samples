
import sys
import numpy as np
from gensim.corpora import Dictionary
from keras.models import load_model

file_prefix = sys.argv[1]
num = int(sys.argv[2])

gen = load_model(f'{file_prefix}.gen')
dic = Dictionary.load(f'{file_prefix}.dic')

def answer():
    x = np.random.normal(0, 1, (1,) + gen.input_shape[1:])

    y = gen.predict(x)

    res = [dic[np.argmax(w)] for w in y[0]]

    return ''.join(res)

for _ in range(num):
    print( answer() )
    print('-----')
