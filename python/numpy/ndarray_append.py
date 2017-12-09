import numpy as np

d = np.arange(5).reshape(5, 1)

print(d)

print(np.append(d[1:], np.array([[10]]), axis = 0))
