
import sys
import os
import glob
import cv2
import numpy as np

files = sys.argv[1]
line_size = 2

for file in glob.glob(files):

    img = cv2.imread(file, 0)

    _, ct, _ = cv2.findContours(img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    dest_img = np.zeros(img.shape)

    dest_img = cv2.drawContours(dest_img, ct, -1, 255, line_size)

    f, ext = os.path.splitext(file)
    dest_file = f"{f}_edge{ext}"

    cv2.imwrite(dest_file, dest_img)

    print(f"done: {f}")
