
import sys
from gensim.models.doc2vec import Doc2Vec, TaggedLineDocument

model_file = sys.argv[1]
data_file = sys.argv[2]
words = sys.argv[3].split(' ')

steps = 50

docs_list = list(TaggedLineDocument(data_file))

model = Doc2Vec.load(model_file)

to_docstr = lambda x: ' '.join(docs_list[x].words)

print(f'--- similar : {to_docstr(0)} ---')

for i, p in model.docvecs.most_similar(0):
    print(f'{p}, {to_docstr(i)}')

print('')
print(f'--- similar : {words} ---')

x = model.infer_vector(words, steps = steps)

for tag, p in model.docvecs.most_similar([x]):
    print(f'{p}, {to_docstr(tag)}')
