import sys
import numpy as np
import pandas as pd
from gensim.corpora import Dictionary
from keras.models import Model
from keras.layers import Input, Dense, GRU

data_file = sys.argv[1]
dest_file_prefix = sys.argv[2]
epoch = int(sys.argv[3])
batch = int(sys.argv[4])
n_hidden = int(sys.argv[5])

BOS = '\t'
EOS = '\n'

df = pd.read_csv(data_file, delimiter = '\t', 
                    names = ('keyword', 'sentence'), dtype = 'object')

keywords = [k.split(' ') for k in df['keyword'].values]
sentences = [[BOS] + s.split(' ') + [EOS] for s in df['sentence'].values]

q_maxlen = np.max([len(q) for q in keywords])
a_maxlen = np.max([len(a) for a in sentences])

print(f'question max size: {q_maxlen}, answer max size: {a_maxlen}')

dic = Dictionary(keywords + sentences)
dic.save(f'{dest_file_prefix}.dic')

padding_one_hot = lambda d, size: np.vstack((
    np.eye(len(dic))[dic.doc2idx(d)],
    np.zeros((size - len(d), len(dic)))
))

x1 = np.array([padding_one_hot(q, q_maxlen) for q in keywords])
x2 = np.array([padding_one_hot(a, a_maxlen) for a in sentences])
y = np.array([np.vstack((d[1:], np.zeros((1, len(dic))))) for d in x2])

encoder = GRU(n_hidden, return_state = True)
enc_inputs = Input(shape = (None, len(dic)))
enc_outputs, enc_h = encoder(enc_inputs)

enc_states = [enc_h]

decoder = GRU(n_hidden, return_sequences = True, return_state = True)
dec_inputs = Input(shape = (None, len(dic)))
dec_outputs, _ = decoder(dec_inputs, initial_state = enc_states)

decoder_dense = Dense(len(dic), activation = 'softmax')
dec_outputs = decoder_dense(dec_outputs)

model = Model([enc_inputs, dec_inputs], dec_outputs)

model.compile(loss = 'categorical_crossentropy', optimizer = 'adam', 
                metrics = ['acc'])

model.fit([x1, x2], y, epochs = epoch, batch_size = batch)

encode_model = Model(enc_inputs, enc_states)

dec_p_input = Input(shape = (n_hidden,))

dec_p_outputs, dec_p_state = decoder(dec_inputs, initial_state = [dec_p_input])

dec_p_outputs = decoder_dense(dec_p_outputs)

decode_model = Model([dec_inputs, dec_p_input], [dec_p_outputs, dec_p_state])

encode_model.save(f'{dest_file_prefix}_encode.h5')
decode_model.save(f'{dest_file_prefix}_decode.h5')
