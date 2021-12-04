from fugashi import Tagger
import sys

tagger = Tagger()

for w in tagger(sys.argv[1]):
    print(f'{w.surface}, {w.feature}')
