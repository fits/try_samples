from fugashi import GenericTagger
import sys

text = sys.argv[1]

tagger = GenericTagger()

for t in tagger(text):
    pos = t.feature[0:4]
    print(f"term={t.surface}, partOfSpeech={pos}")
