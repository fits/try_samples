from fugashi import Tagger
import unidic
import sys

tagger = Tagger(f'-d "{unidic.DICDIR}"')

for w in tagger(sys.argv[1]):
    print(w.surface, ", ", w.feature)
