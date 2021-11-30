import MeCab
import sys

tagger = MeCab.Tagger()

for line in tagger.parse(sys.argv[1]).splitlines():
    if line != "EOS":
        surface, feature = line.split("\t", 1)
        print(surface, ", <", feature, ">")
