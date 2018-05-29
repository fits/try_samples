
import sys
from gensim.models.doc2vec import Doc2Vec, TaggedLineDocument

file = sys.argv[1]
epochs = int(sys.argv[2])
words = sys.argv[3].split(' ')

steps = 50

docs = TaggedLineDocument(file)

model = Doc2Vec(docs, min_count = 1, epochs = epochs)

docs_list = list(docs)

to_docstr = lambda x: ' '.join(docs_list[x].words)

print(f'--- similar : {to_docstr(0)} ---')

for i, p in model.docvecs.most_similar(0):
    print(f'{p}, {to_docstr(i)}')

print('')
print(f'--- similar : {words} ---')

x = model.infer_vector(words, steps = steps)

for tag, p in model.docvecs.most_similar([x]):
    print(f'{p}, {to_docstr(tag)}')
