
from functools import partial
from operator import add

plus3 = partial(add, 3)

print(f"3 + 4 = {plus3(4)}")
