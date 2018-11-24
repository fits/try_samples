
import sys
import os
import glob
import subprocess

size = sys.argv[1]
targets = sys.argv[2]
dest_dir = sys.argv[3]

for f in glob.glob(targets):
    file, ext = os.path.splitext(os.path.basename(f))

    dest_file = f"{dest_dir}/{file}_{size}{ext}"

    cmd = f"convert {f} -gravity center -crop {size}+0+0 {dest_file}"

    ret = subprocess.call(cmd, shell = True)

    print(f"done {f}: {ret}")
