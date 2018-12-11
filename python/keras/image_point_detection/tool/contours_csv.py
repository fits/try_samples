
import sys
import os
import glob
import json
import cv2

files = sys.argv[1]
dest_dir = sys.argv[2]

for f in glob.glob(files):
    img = cv2.imread(f, 0)

    _, cts, _ = cv2.findContours(img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    lines = [f"{int(c[0][0])},{int(c[0][1])}" for cs in cts for c in cs]

    dest_file = f"{dest_dir}/{os.path.splitext(os.path.basename(f))[0]}.csv"

    with open(dest_file, mode = 'w') as fo:
        fo.write('\n'.join(lines) + '\n')

    print(f"created: {dest_file}")
