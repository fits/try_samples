
import sys
import os
import glob
import subprocess

targets = sys.argv[1]
size = sys.argv[2]
background = sys.argv[3]

for f in glob.glob(targets):

    file, ext = os.path.splitext(f)
    dest_file = f"{file}_{size}{ext}"

    cmd = f"convert {f} -background {background} -gravity center -extent {size} {dest_file}"

    ret = subprocess.call(cmd, shell = True)

    print(f"done {f}: {ret}")
