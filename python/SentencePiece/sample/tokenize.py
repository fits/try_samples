import sentencepiece as spm
import sys

model = sys.argv[1]

s = spm.SentencePieceProcessor(model_file=model)

tokens = s.encode(sys.argv[2], out_type = str)

print(tokens)
