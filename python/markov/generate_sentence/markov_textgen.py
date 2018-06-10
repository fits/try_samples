
import sys
import codecs
from collections import Counter
import numpy as np

EOS = '\n'

data_file = sys.argv[1]
n_words = int(sys.argv[2])
size = int(sys.argv[3])
first_word = sys.argv[4]

def read_line(file):
    with codecs.open(file, 'r', 'utf-8') as f:
        return [line.strip().split() for line in f.readlines()]

def split_sentence(ws, size):
    for i in range(len(ws) - size + 1):
        yield tuple(ws[i:i + size])

def select(word, counter):
    cd = list(filter(lambda x: x[0] == word, counter.keys()))

    if (len(cd) == 0):
        return (EOS,)

    probs = np.array([counter[c] for c in cd], dtype = float)
    probs /= probs.sum()

    return cd[np.random.choice(len(cd), p = probs)]

def generate(counter, fst_word, maxlen = 50):
    ch = fst_word
    res = []

    for _ in range(maxlen):
        ws = select(ch, counter)

        res.extend(list(ws[0:-1]))

        ch = ws[-1]

        if ch == EOS:
            break

    return res


data = [
    t for ss in read_line(data_file)
      for t in split_sentence(ss + [EOS], n_words)
]

fq = Counter(data)

for _ in range(size):
    print(''.join(generate(fq, first_word)))
    print('-----')
