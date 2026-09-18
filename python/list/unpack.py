def show1(a, b, c):
    print(f"a={a}, b={b}, c={c}")

d1 = [1, 2, 3]
d2 = { 'a': 4, 'b': 5, 'c': 6 }
d3 = { 'd': 7, 'e': 8, 'f': 9 }
d4 = (1, 2, 3)

show1(*d1)
show1(*d2)
show1(**d2)
show1(*d3)
# show1(**d3) # error
show1(*d4)
