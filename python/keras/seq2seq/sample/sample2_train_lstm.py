import sys
import pandas as pd
import numpy as np
from gensim.corpora import Dictionary
from keras.models import Model
from keras.layers import Input, Dense, LSTM

data_file = sys.argv[1]
dest_file_prefix = sys.argv[2]
epoch = int(sys.argv[3])
batch = int(sys.argv[4])
n_hidden = 128

BOS = '\t'
EOS = '\n'

df = pd.read_csv(data_file, names = ('question', 'answer'), dtype = 'object')

q_maxlen = df['question'].map(len).max()
a_maxlen = df['answer'].map(len).max() + 2

ans = df['answer'].map(lambda a: f'{BOS}{a}{EOS}')

dic = Dictionary([list(BOS + EOS + ' '.join(df.values.flatten()))])
dic.save(f'{data_file}.dic2')

padding_one_hot = lambda d, size: np.vstack((
    np.eye(len(dic))[dic.doc2idx(list(d))],
    np.zeros((size - len(d), len(dic)))
))

x1 = np.array([padding_one_hot(q, q_maxlen) for q in df['question']])
x2 = np.array([padding_one_hot(a, a_maxlen) for a in ans])
y = np.array([np.vstack((d[1:], np.zeros((1, len(dic))))) for d in x2])


encoder = LSTM(n_hidden, return_state = True)
enc_inputs = Input(shape = (None, len(dic)))
enc_outputs, enc_h, enc_c = encoder(enc_inputs)

enc_states = [enc_h, enc_c]

decoder = LSTM(n_hidden, return_sequences = True, return_state = True)
dec_inputs = Input(shape = (None, len(dic)))
dec_outputs, _, _ = decoder(dec_inputs, initial_state = enc_states)

decoder_dense = Dense(len(dic), activation = 'softmax')
dec_outputs = decoder_dense(dec_outputs)

model = Model([enc_inputs, dec_inputs], dec_outputs)

model.compile(loss = 'categorical_crossentropy', optimizer = 'adam', 
                metrics = ['accuracy'])

model.fit([x1, x2], y, epochs = epoch, batch_size = batch)


encode_model = Model(enc_inputs, enc_states)

dec_p_input_h = Input(shape = (n_hidden,))
dec_p_input_c = Input(shape = (n_hidden,))

dec_p_inputs = [dec_p_input_h, dec_p_input_c]

dec_p_outputs, dec_p_h, dec_p_c = decoder(dec_inputs, initial_state = dec_p_inputs)

dec_p_states = [dec_p_h, dec_p_c]

dec_p_outputs = decoder_dense(dec_p_outputs)

decode_model = Model([dec_inputs] + dec_p_inputs, [dec_p_outputs] + dec_p_states)

encode_model.save(f'{dest_file_prefix}_encode.h5')
decode_model.save(f'{dest_file_prefix}_decode.h5')
