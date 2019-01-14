
import sys
import os
import glob
import numpy as np
from keras.preprocessing.image import load_img, img_to_array
from keras.models import load_model
import cv2

model_file = sys.argv[1]
img_files = sys.argv[2]
dest_dir = sys.argv[3]

model = load_model(model_file)

for f in glob.glob(img_files):
    img = img_to_array(load_img(f))

    pred = model.predict(np.array([img]))[0]
    pred *= 255

    file, ext = os.path.splitext(os.path.basename(f))

    cv2.imwrite(f"{dest_dir}/{file}_predict.png", pred)

    print(f"done: {f}")
