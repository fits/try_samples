import numpy as np

a = np.array([80, 40, 60, 10, 20, 70, 50, 30, 90])

n = 3

print( np.argsort(a)[:n] )
print( a[np.argsort(a)[:n]] ) # 10 20 30

print('-----')

print( np.argsort(-a)[:n] )
print( a[np.argsort(-a)[:n]] ) # 90 80 70
