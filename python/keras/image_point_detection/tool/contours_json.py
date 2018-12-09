
import sys
import json
import cv2

file = sys.argv[1]

img = cv2.imread(file, 0)

_, cts, _ = cv2.findContours(img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

res = json.dumps([ {"x": int(c[0][0]), "y": int(c[0][1])} for cs in cts for c in cs ])

print(res)
