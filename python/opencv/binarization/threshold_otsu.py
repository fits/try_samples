
import sys
import cv2

file = sys.argv[1]
dest_file = sys.argv[2]

img = cv2.imread(file, 0)

blur = cv2.GaussianBlur(img, (3, 3), 0)

_, dest_img = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)

cv2.imwrite(dest_file, dest_img)
