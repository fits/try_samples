import numpy as np
from gensim.corpora import Dictionary
from keras.models import Sequential
from keras.layers import LSTM
from keras.layers.core import Dense, Activation, RepeatVector
from keras.layers.wrappers import TimeDistributed

epoch = 100
batch = 50
n = 2000
digit = 2
input_digit = digit * 2 + 1
output_digit = digit + 1

limit = int('9' * digit) + 1

input_format = lambda f: f.ljust(input_digit, ' ')
output_format = lambda f: f.ljust(output_digit, ' ')

input_formula = lambda d: input_format(f"{d[0]}+{d[1]}")

ds = np.random.randint(0, limit, size = (n, 2))

data = [input_formula(d) for d in ds]
labels = [output_format(f"{sum(d)}") for d in ds]

dic = Dictionary([list('0123456789+ ')])

one_hot = lambda ds: np.array([np.eye(len(dic))[dic.doc2idx(list(d))] for d in ds])

x = one_hot(data)
y = one_hot(labels)


model = Sequential()

# encoder
model.add(LSTM(128, input_shape=(input_digit, len(dic))))

# decoder
model.add(RepeatVector(output_digit))
model.add(LSTM(128, return_sequences = True))

model.add(TimeDistributed(Dense(len(dic))))
model.add(Activation('softmax'))

model.compile(loss = 'categorical_crossentropy', optimizer = 'adam', metrics = ['accuracy'])

model.fit(x, y, epochs = epoch, batch_size = batch)


predict = lambda f: model.predict(one_hot([input_format(f)]))
to_answer = lambda rs: ''.join([dic[np.argmax(r)] for r in rs])

def new_question(size):
  c = 0

  while c < size:
    ns = np.random.randint(0, limit, size = (1, 2))[0]
    q = input_formula(ns)

    if q not in data:
      c = c + 1
      yield q

print('-----')

for q in new_question(10):
  r = to_answer(predict(q)[0])
  print(f"{q} = {r}")

print('-----')

qlist = ['1+2', '2+1', '5+5', '3+2', '1+0']

for q in qlist:
  r = to_answer(predict(q)[0])
  print(f"{q} = {r}")
