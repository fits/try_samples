from collections import Counter

d = [1, 1, 2, 2, 3, 4, 3, 1, 1, 1, 2, 2]

c = Counter(d)

for k in sorted(list(c.keys())):
    print(f"{k} : {c[k]}")
