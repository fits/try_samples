
import sys
import numpy as np
import cv2
from datetime import datetime

n = int(sys.argv[1])
file = sys.argv[2]

video = cv2.VideoCapture(file)

def read_frame(frame_num):
    video.set(cv2.CAP_PROP_POS_FRAMES, frame_num)
    return video.read()[1]

frame_count = video.get(cv2.CAP_PROP_FRAME_COUNT)

imgs = [read_frame(f) for f in np.random.permutation(int(frame_count))[:n]]

video.release()

img = cv2.hconcat(imgs)

name = datetime.now().strftime('%Y%m%d-%H%M%S')

cv2.imwrite(f'temp/{name}.jpg', img)
