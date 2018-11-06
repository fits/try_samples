
import sys
import os
import glob
import numpy as np
from keras.preprocessing.image import load_img, img_to_array
from keras.models import load_model
import cv2

model_file = sys.argv[1]
img_file_pattern = sys.argv[2]
dest_dir = sys.argv[3]

line_color = (0, 0, 255)
line_size = 3

files = glob.glob(img_file_pattern)

model = load_model(model_file)

for f in files:

    img = img_to_array(load_img(f, color_mode = 'grayscale'))

    res = model.predict(np.array([img])).flatten().tolist()

    print(res)

    p = list(map(int, res))

    cvimg = cv2.imread(f)

    cvimg = cv2.line(cvimg, (p[0], p[1]), (p[2], p[3]), line_color, line_size)
    cvimg = cv2.line(cvimg, (p[4], p[5]), (p[6], p[7]), line_color, line_size)

    cv2.imwrite(f"{dest_dir}/{os.path.basename(f)}", cvimg)
