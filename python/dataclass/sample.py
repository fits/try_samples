
from dataclasses import dataclass, replace

@dataclass(frozen = True)
class Item:
    name: str
    value: int = 0

d1 = Item('a1', 5)

print(d1)

d2 = replace(d1, value = 3)

print(d2)

if isinstance(d1, Item):
    print('type is Item')
