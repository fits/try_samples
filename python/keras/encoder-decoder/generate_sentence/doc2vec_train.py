import sys
from gensim.models.doc2vec import Doc2Vec, TaggedLineDocument

data_file = sys.argv[1]
dest_file = sys.argv[2]

epochs = int(sys.argv[3])
min_count = int(sys.argv[4])

docs = TaggedLineDocument(data_file)

model = Doc2Vec(docs, min_count = min_count, epochs = epochs)

model.save(dest_file)
