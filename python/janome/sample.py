import sys
from janome.tokenizer import Tokenizer

msg = sys.argv[1]
tokenizer = Tokenizer()

for token in tokenizer.tokenize(msg):
    print(token)
