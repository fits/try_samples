
from functools import reduce

r = reduce(lambda a, b: a * b, [2, 3, 4, 5])

print(f"result = {r}")
