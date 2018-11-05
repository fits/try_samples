
import sys
import os
import cv2

file = sys.argv[1]
dest_dir = sys.argv[2]

img = cv2.imread(file)

img = cv2.line(img, (100, 50), (100, 250), (0, 0, 255), 4)

cv2.imwrite(f"{dest_dir}/{os.path.basename(file)}", img)
