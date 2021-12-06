import sentencepiece as spm
import sys

model = 'ja.wiki.bpe.vs200000.model'

s = spm.SentencePieceProcessor(model_file = model)

for n in range(5):
    tokens = s.encode(sys.argv[1], out_type = str, enable_sampling = True)
    print(tokens)
