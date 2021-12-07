import sentencepiece as spm
import sys

file = sys.argv[1]
mprefix = sys.argv[2]
vsize = int(sys.argv[3])
mtype = sys.argv[4]

spm.SentencePieceTrainer.train(
    input = file, 
    model_prefix = mprefix, 
    vocab_size = vsize, 
    model_type = mtype
)
