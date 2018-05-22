import numpy as np

a = np.array([80, 40, 60, 10, 20, 70, 50, 30, 90])

n = 3

print( np.argpartition(a, n)[:n] )
print( a[np.argpartition(a, n)[:n]] ) # 20 10 30

print('-----')

print( np.argpartition(-a, n)[:n] )
print( a[np.argpartition(-a, n)[:n]] ) # 90 80 70
