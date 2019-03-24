
import sys
import os
import numpy as np
import cv2

imgfile = sys.argv[1]

def otsu(img):
    th, _ = cv2.threshold(img.copy(), 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
    return th

def binalize(img, th):
    _, res = cv2.threshold(img.copy(), th, 255, cv2.THRESH_BINARY_INV)
    return res

def detect_threshold(img, bth):
    white_area = lambda g: len(g[g > 127])

    areas = [white_area(binalize(img, t)) for t in range(0, 254)]

    grd1 = np.gradient(areas)
    grd2 = np.gradient(grd1)

    start = bth
    end = np.max(np.where(grd2 < 0.5))

    mx = np.argmax(grd1[start:end]) + start

    return np.argmin(grd1[mx:end]) + mx


img = cv2.imread(imgfile, 0)
img = cv2.GaussianBlur(img, (3, 3), 0)

base_th = int(otsu(img))

th = detect_threshold(img, base_th)

print(f'otsu: {base_th}, result: {th}')

resimg = binalize(img, th)

f, ext = os.path.splitext(imgfile)

cv2.imwrite(f"{f}.png", resimg)
