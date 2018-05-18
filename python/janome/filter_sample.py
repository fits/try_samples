import sys
from janome.analyzer import Analyzer
from janome.tokenfilter import *

msg = sys.argv[1]

filters = [POSKeepFilter('名詞')]

analyzer = Analyzer(token_filters = filters)

for t in analyzer.analyze(msg):
    print(f'phonetic = {t.phonetic}, reading = {t.reading}, surface = {t.surface}, part_of_speech = {t.part_of_speech}, base_form = {t.base_form}, infl_form = {t.infl_form}, infl_type = {t.infl_type}, node_type = {t.node_type}')
