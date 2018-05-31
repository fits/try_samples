
import sys
import numpy as np
from gensim.models.doc2vec import TaggedLineDocument
from gensim.models.word2vec import Word2Vec
from keras.layers import Input, Dense, Reshape, Flatten, Dropout, GRU
from keras.models import Sequential, Model
from keras.optimizers import Adam

data_file = sys.argv[1]
dest_file_prefix = sys.argv[2]

epoch = int(sys.argv[3])
batch = int(sys.argv[4])

wv_size = 200
wv_epoch = 2000

num_unit = 512
input_size = (10,)

docs = TaggedLineDocument(data_file)

words = [d.words for d in docs]

wv_model = Word2Vec(words, wv_size, min_count = 1, iter = wv_epoch)

input_size += (wv_model.vector_size,)

word_maxlen = np.max([len(w) for w in words])

def discriminator(input_shape):
    model = Sequential()
    
    model.add(GRU(num_unit, input_shape = input_shape))
    model.add(Dropout(0.3))
    model.add(Dense(num_unit, activation = 'relu'))
    model.add(Dense(1, activation = 'sigmoid'))
    
    model.summary()
    
    input = Input(shape = input_shape)
    
    return Model(input, model(input))

def generator(input_shape, output_shape):
    model = Sequential()

    model.add(GRU(num_unit, input_shape = input_shape, return_sequences = True))
    model.add(Dropout(0.3))
    model.add(Flatten())
    model.add(Dense(num_unit, activation = 'relu'))
    model.add(Dropout(0.3))
    model.add(Dense(np.prod(output_shape), activation = 'relu'))
    model.add(Reshape(output_shape))
    
    model.summary()

    input = Input(shape = input_shape)
    
    return Model(input, model(input))


dis_opt = Adam(lr = 1e-5, beta_1 = 0.1)

dis = discriminator((word_maxlen, wv_model.vector_size))
dis.compile(loss = 'binary_crossentropy', optimizer = dis_opt, metrics = ['acc'])
dis.trainable = False

gen = generator(input_size, (word_maxlen, wv_model.vector_size))

z = Input(shape = input_size)
r = dis(gen(z))

m_opt = Adam(lr = 2e-4, beta_1 = 0.5)

model = Model(z, r)
model.compile(loss = 'binary_crossentropy', optimizer = m_opt)

padding_wv = lambda d, size: np.vstack((
    wv_model.wv[d],
    np.zeros((size - len(d), wv_model.vector_size))
))

def train(epochs, batch_size):
    valid = np.ones((batch_size, 1))
    fake = np.zeros((batch_size, 1))

    for ep in range(epochs):
        idx = np.random.randint(0, len(words), batch_size)
        data = np.array([padding_wv(w, word_maxlen) for w in np.array(words)[idx]])

        noise = np.random.normal(0, 1, (batch_size,) + input_size)

        gen_data = gen.predict(noise)

        dis_loss_valid = dis.train_on_batch(data, valid)
        dis_loss_fake = dis.train_on_batch(gen_data, fake)

        dis_loss, dis_acc = 0.5 * np.add(dis_loss_valid, dis_loss_fake)

        noise = np.random.normal(0, 1, (batch_size,) + input_size)

        model_loss = model.train_on_batch(noise, valid)

        print(f'{ep} - model loss: {model_loss}, dis loss: {dis_loss}, dis acc: {dis_acc}')


train(epoch, batch)

wv_model.save(f'{dest_file_prefix}.wv')
gen.save(f'{dest_file_prefix}.gen')
