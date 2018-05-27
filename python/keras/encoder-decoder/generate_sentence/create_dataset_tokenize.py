import sys
import numpy as np
from janome.tokenizer import Tokenizer

data_file = sys.argv[1]
targets = sys.argv[2].split(';')

tokenizer = Tokenizer()

lines = [line.rstrip('\n') for line in open(data_file)]

is_target = lambda token: np.any([
    token.part_of_speech.startswith(trg) for trg in targets
])

tokens = [tokenizer.tokenize(s) for line in lines for s in line.split('。')]

docs = [
    (
        [t.surface for t in ts if is_target(t)], 
        [t.surface for t in ts]
    )
    for ts in tokens
]

join_by_blank = lambda ws: ' '.join(ws)

for q, a in docs:
    if len(q) > 0:
        sys.stdout.buffer.write(
            f'{join_by_blank(q)}\t{join_by_blank(a)}\n'.encode('utf-8')
        )
