
import sys
import os
import glob
import numpy as np
from keras.preprocessing.image import load_img, img_to_array
from keras.models import load_model
import cv2

color_set = [(255, 255, 255), (255, 75, 0), (255, 241, 0), (3, 175, 122), 
             (0, 90, 255), (77, 196, 255), (255, 128, 130), (246, 170, 0), 
             (153, 0, 153), (128, 64, 0)]

cv_color_set = [(c[2], c[1], c[0]) for c in color_set]

p = 0.7

model_file = sys.argv[1]
trg_imgs = sys.argv[2]
dest_dir = sys.argv[3]

model = load_model(model_file)

for f in glob.glob(trg_imgs):
    img = img_to_array(load_img(f))

    prd = model.predict(np.array([img]))[0]

    img1 = np.apply_along_axis(lambda x: cv_color_set[x.argmax()], -1, prd)

    img2 = cv2.addWeighted(img.astype(int), 1 - p, img1, p, 0)

    file, ext = os.path.splitext(os.path.basename(f))

    cv2.imwrite(f"{dest_dir}/{file}_points.png", img2)

    print(f"done: {f}")
