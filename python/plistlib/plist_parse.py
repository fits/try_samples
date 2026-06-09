import plistlib
import sys

plist_file = sys.argv[1]

with open(plist_file, 'rb') as f:
    s = plistlib.load(f)

    print(s)
