import sys
import numpy as np

n = int(sys.argv[1])
digit = int(sys.argv[2])

limit = int('9' * digit) + 1

for d in np.random.randint(0, limit, size = (n, 2)):
    print(f"{d[0]}+{d[1]},{sum(d)}")
