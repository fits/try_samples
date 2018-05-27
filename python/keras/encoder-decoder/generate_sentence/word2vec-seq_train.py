import sys
import numpy as np
import pandas as pd
from gensim.models import word2vec
from keras.models import Model
from keras.layers import Input, Dense, GRU, Flatten

data_file = sys.argv[1]
dest_file_prefix = sys.argv[2]
epoch = int(sys.argv[3])
batch = int(sys.argv[4])
n_hidden = int(sys.argv[5])

wv_size = int(sys.argv[6])
wv_iter = int(sys.argv[7])

BOS = '\t'
EOS = '\n'

df = pd.read_csv(data_file, delimiter = '\t', 
                    names = ('keyword', 'sentence'), dtype = 'object')

keywords = [k.split(' ') for k in df['keyword'].values]
sentences = [[BOS] + s.split(' ') + [EOS] for s in df['sentence'].values]

wv_model = word2vec.Word2Vec(sentences, wv_size, min_count = 1, iter = wv_iter)
wv_model.save(f'{data_file}.wv')

q_maxlen = np.max([len(q) for q in keywords])
a_maxlen = np.max([len(a) for a in sentences])

print(f'question max size: {q_maxlen}, answer max size: {a_maxlen}')

padding_wv = lambda d, size: np.vstack((
    wv_model.wv[d],
    np.zeros((size - len(d), wv_model.vector_size))
))

x1 = np.array([padding_wv(q, q_maxlen) for q in keywords])
x2 = np.array([padding_wv(a, a_maxlen) for a in sentences])
y = np.array([np.vstack((d[1:], np.zeros((1, wv_model.vector_size)))) for d in x2])

enc_inputs = Input(batch_shape = (None, q_maxlen, wv_model.vector_size))
enc_outputs = Dense(n_hidden, activation = 'relu')(
    Flatten()(
        Dense(n_hidden, activation = 'relu')(enc_inputs)
    )
)

enc_states = [enc_outputs]

decoder = GRU(n_hidden, return_sequences = True, return_state = True)
dec_inputs = Input(shape = (None, wv_model.vector_size))
dec_outputs, _ = decoder(dec_inputs, initial_state = enc_states)

decoder_dense = Dense(wv_model.vector_size, activation = 'relu')
dec_outputs = decoder_dense(dec_outputs)

model = Model([enc_inputs, dec_inputs], dec_outputs)

model.compile(loss = 'mse', optimizer = 'adam', metrics = ['mae', 'acc'])

model.fit([x1, x2], y, epochs = epoch, batch_size = batch)

encode_model = Model(enc_inputs, enc_states)

dec_p_input = Input(shape = (n_hidden,))

dec_p_outputs, dec_p_state = decoder(dec_inputs, initial_state = [dec_p_input])

dec_p_outputs = decoder_dense(dec_p_outputs)

decode_model = Model([dec_inputs, dec_p_input], [dec_p_outputs, dec_p_state])

encode_model.save(f'{dest_file_prefix}_encode.h5')
decode_model.save(f'{dest_file_prefix}_decode.h5')
