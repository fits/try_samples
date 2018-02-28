
import numpy as np

def f(x):
    print(f"param: {x}")
    return str(x)

tostr = np.frompyfunc(f, 1, 1)

print( tostr(np.array([1, 2, 3])) )

print( tostr(np.array([ [1, 2], [3, 4] ])) )
