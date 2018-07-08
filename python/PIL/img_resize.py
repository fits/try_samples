
import sys
from PIL import Image

img_file = sys.argv[1]
w = int(sys.argv[2])
h = int(sys.argv[3])
dest_file = sys.argv[4]

Image.open(img_file).resize((w, h)).save(dest_file)
