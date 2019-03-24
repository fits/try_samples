
import sys
import cv2
import numpy as np

file = sys.argv[1]

def opencv_otsu(img):
    res, _ = cv2.threshold(img, 0, 255, cv2.THRESH_OTSU)
    return res

def otsu(img):
    n = len(img)
    u = np.mean(img)

    def compute(t):
        c1 = img[img <= t]
        c1_n = len(c1)

        c2 = img[img > t]
        c2_n = len(c2)

        if c1_n is 0 or c2_n is 0:
            return 0

        w = (c1_n * np.var(c1) + c2_n * np.var(c2)) / n
        b = (c1_n * (np.mean(c1) - u) ** 2 + c2_n * (np.mean(c2) - u) ** 2) / n

        return b / w

    return np.argmax([compute(i) for i in range(256)])


img = cv2.imread(file, 0)

blur = cv2.GaussianBlur(img, (3, 3), 0)

print(f'otsu: {otsu(blur)}, opencv otsu: {opencv_otsu(blur)}')
