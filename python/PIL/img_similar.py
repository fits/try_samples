
import glob
import sys
import numpy as np
from PIL import Image

img_dir = sys.argv[1]
target_img_file = sys.argv[2]

w = 64
h = 64

#to_vec = lambda x: np.array(Image.open(x).resize((w, h))) / 255
to_vec = lambda x: np.array(Image.open(x).resize((w, h), Image.LANCZOS)) / 255

imgs = [(f, to_vec(f)) for f in glob.glob(f'{img_dir}/*.jpg')]

trg = to_vec(target_img_file)

dis = [(f, np.linalg.norm(trg - v)) for f, v in imgs]

for r in sorted(dis, key = lambda x: x[1]):
    print(r)
