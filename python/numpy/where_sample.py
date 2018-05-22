import numpy as np

a = np.array([0, 1, 2, 3, 2, 1, 0])

print( np.where(a < 2) )

for i in np.where(a < 2)[0]:
    print(i)
