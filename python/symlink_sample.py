import os
import sys

src = sys.argv[1]
trg = sys.argv[2]

backup = f'{trg}_backup'

try:
    os.replace(trg, backup)
except FileNotFoundError:
    pass

try:
    os.symlink(src, trg)
except OSError as e:
    print(e)

    if os.path.isfile(backup):
        os.replace(backup, trg)

    sys.exit(1)
finally:
    if os.path.isfile(backup):
        os.unlink(backup)
