
import glob
import sys
import numpy as np
from PIL import Image
from keras.models import Model
from keras.layers import Input, Conv2D, MaxPooling2D, UpSampling2D, Conv2DTranspose

img_dir = sys.argv[1]
target_img_file = sys.argv[2]

epochs = 50
batch_size = 10

img_size = (640, 480)

to_vec = lambda x: np.array(Image.open(x).resize(img_size, Image.LANCZOS)) / 255

imgs = [(f, to_vec(f)) for f in glob.glob(f'{img_dir}/*.jpg')]

imgs_vec = np.array([v for f, v in imgs])

input = Input(shape = imgs_vec.shape[1:])

x = Conv2D(16, (4, 4), padding = 'same')(input)
x = MaxPooling2D()(x)
x = Conv2D(8, (2, 2), padding = 'same')(x)
x = MaxPooling2D()(x)
x = Conv2D(4, (2, 2), padding = 'same')(x)
x = MaxPooling2D()(x)

encoder = Model(input = input, output = x)

x = UpSampling2D()(x)
x = Conv2DTranspose(8, (2, 2), padding = 'same')(x)
x = UpSampling2D()(x)
x = Conv2DTranspose(16, (2, 2), padding = 'same')(x)
x = UpSampling2D()(x)
x = Conv2DTranspose(3, (4, 4), padding = 'same')(x)

autoencoder = Model(input = input, output = x)

encoder.summary()
autoencoder.summary()

autoencoder.compile(loss = 'mse', optimizer = 'adam', metrics = ['mae', 'acc'])

autoencoder.fit(imgs_vec, imgs_vec, epochs = epochs, batch_size = batch_size)

print('----------')

enc_imgs = [(f, encoder.predict(np.array([v]))[0]) for f, v in imgs]

trg = encoder.predict(np.array([to_vec(target_img_file)]))[0]

dis = [(f, np.linalg.norm(trg - v)) for f, v in enc_imgs]

for r in sorted(dis, key = lambda x: x[1]):
    print(r)
