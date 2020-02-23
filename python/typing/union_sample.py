
from dataclasses import dataclass
from typing import Union

@dataclass(frozen = True)
class Created:
    item: str
    qty: int = 0

@dataclass(frozen = True)
class Shipped:
    item: str
    shipped_qty: int = 0

@dataclass(frozen = True)
class Arrived:
    item: str

Event = Union[Created, Shipped]

def f(x: Event):
    if isinstance(x, Created):
        print(f"created: item={x.item}, qty={x.qty}")
    elif isinstance(x, Shipped):
        print(f"shipped: item={x.item}, shipped_qty={x.shipped_qty}")
    else:
        print(f"other: {x}")

print(Event)

f(Created('item-1', 1))
f(Shipped('item-1', 3))

f(Arrived('item-1'))
