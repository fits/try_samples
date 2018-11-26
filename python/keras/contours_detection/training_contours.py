
import sys
import glob
import numpy as np
from keras.preprocessing.image import load_img, img_to_array
from keras.models import load_model

label_threshold = 128

model_file = sys.argv[1]
data_imgs = sys.argv[2]
label_imgs = sys.argv[3]
epochs = int(sys.argv[4])
batch_size = int(sys.argv[5])
dest_file = sys.argv[6]

imgs = np.array([img_to_array(load_img(f)) for f in glob.glob(data_imgs)])

labels = np.array([
    img_to_array(load_img(f, color_mode = 'grayscale')) 
    for f in glob.glob(label_imgs)
])

labels[labels < label_threshold] = 0
labels[labels >= label_threshold] = 1

print(f"data shape: {imgs.shape}, label shape: {labels.shape}")
print(f"label: 0 = {len(labels[labels == 0])}, 1 = {len(labels[labels == 1])}")

model = load_model(model_file)

model.fit(imgs, labels, epochs = epochs, batch_size = batch_size)

model.save(dest_file)
