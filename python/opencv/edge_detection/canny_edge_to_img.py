
import sys
import os
import glob
import cv2

files = sys.argv[1]

for f in glob.glob(files):

    img = cv2.imread(f)

    dest_img = cv2.Canny(img, 100, 200)

    file, ext = os.path.splitext(f)
    dest_file = f"{file}_edge{ext}"

    cv2.imwrite(dest_file, dest_img)

    print(f"done: {f}")
