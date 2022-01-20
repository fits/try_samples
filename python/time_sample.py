import time
from datetime import datetime

t1 = datetime.fromisoformat('2022-01-20T21:30:00+09:00').timestamp()
t2 = time.time()

print(f'{t1}, {int(t1)}')
print(f'{t2}, {int(t2)}')

t = int(t1 - t2)

print(t)
