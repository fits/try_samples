import sys
import pandas as pd
import numpy as np
from gensim.corpora import Dictionary
from keras.models import Sequential
from keras.layers import LSTM
from keras.layers.core import Dense, Activation, RepeatVector
from keras.layers.wrappers import TimeDistributed

data_file = sys.argv[1]
dest_file = sys.argv[2]
epoch = int(sys.argv[3])
batch = int(sys.argv[4])
n_hidden = 128

df = pd.read_csv(data_file, names = ('question', 'answer'), dtype = 'object')

q_maxlen = df['question'].map(len).max()
a_maxlen = df['answer'].map(len).max()

rpad_blank = lambda size: (lambda s: s.ljust(size, ' '))

que = df['question'].map(rpad_blank(q_maxlen))
ans = df['answer'].map(rpad_blank(a_maxlen))

dic = Dictionary([list(' '.join(df.values.flatten()))])
dic.save_as_text(f'{data_file}.dic')

one_hot = lambda s: np.eye(len(dic))[dic.doc2idx(list(s))]

x = np.array([one_hot(q) for q in que])
y = np.array([one_hot(a) for a in ans])

model = Sequential()

# encoder
model.add(LSTM(n_hidden, input_shape=(q_maxlen, len(dic))))

# decoder
model.add(RepeatVector(a_maxlen))
model.add(LSTM(n_hidden, return_sequences = True))

model.add(TimeDistributed(Dense(len(dic))))
model.add(Activation('softmax'))

model.compile(loss = 'categorical_crossentropy', 
                optimizer = 'adam', metrics = ['accuracy'])

model.fit(x, y, epochs = epoch, batch_size = batch)

model.save(dest_file)
