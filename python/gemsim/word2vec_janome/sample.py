
import sys
import numpy as np
from janome.tokenizer import Tokenizer
from gensim.models import word2vec

targets = ['名詞', '形容詞', '副詞']

file = sys.argv[1]
wv_iter = int(sys.argv[2])
test_words = sys.argv[3].split(',')

lines = [line.rstrip('\n') for line in open(file)]

tokenizer = Tokenizer()

is_target = lambda token: np.any([
    token.part_of_speech.startswith(trg) for trg in targets
])

sentences = [
    [t.surface for t in tokenizer.tokenize(s) if is_target(t)]
    for line in lines for s in line.split('。')
]

model = word2vec.Word2Vec(sentences, iter = wv_iter)

for w in test_words:
    print(f'--- "{w}" most_similar ---')

    for r in model.wv.most_similar(w):
        print(r)

print('--- predict_output_word ---')

for w in model.predict_output_word(test_words):
    print(w)
