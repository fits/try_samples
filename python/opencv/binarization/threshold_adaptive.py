
import sys
import cv2

file = sys.argv[1]
dest_file = sys.argv[2]

img = cv2.imread(file, 0)

#blur = cv2.medianBlur(img, 3)
blur = cv2.GaussianBlur(img, (3, 3), 0)

dest_img = cv2.adaptiveThreshold(blur, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV, 11, 2)

cv2.imwrite(dest_file, dest_img)
