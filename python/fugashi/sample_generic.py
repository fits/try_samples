from fugashi import GenericTagger
import sys

tagger = GenericTagger()

for w in tagger(sys.argv[1]):
    print(w.surface, ", ", w.feature)
