
from functools import reduce

def divide_list(xs, n):
	q = len(xs) // n
	m = len(xs) % n

	return reduce(
		lambda acc, i:
			(lambda s = reduce(lambda t, x: t + len(x), acc, 0):
				acc + [ xs[s:(s + q + (1 if i < m else 0))] ]
			)(),
		range(n),
		[]
	)

def divide_list1(xs, n):
	q = len(xs) // n
	m = len(xs) % n

	data_size = lambda d: reduce(lambda acc, x: acc + len(x), d, 0)

	take_slice = (lambda d, i:
		slice(
			data_size(d), 
			data_size(d) + q + (1 if i < m else 0)
		)
	)

	return reduce(
		lambda acc, i: acc + [ xs[take_slice(acc, i)] ],
		range(n),
		[]
	)


def divide_list2(xs, n):
	q = len(xs) // n
	m = len(xs) % n

	pos = lambda start, cur: start + q + (1 if cur < m else 0)

	return reduce(
		lambda acc, i:
			( pos(acc[0], i), acc[1] + [ xs[acc[0]:pos(acc[0], i)] ] ),
		range(n),
		(0, [])
	)[1]

lrange = lambda n: list(range(n))

print(divide_list(lrange(8), 3))
print(divide_list(lrange(7), 3))
print(divide_list(lrange(6), 3))

print('-----')

print(divide_list1(lrange(8), 3))
print(divide_list1(lrange(7), 3))
print(divide_list1(lrange(6), 3))

print('-----')

print(divide_list2(lrange(8), 3))
print(divide_list2(lrange(7), 3))
print(divide_list2(lrange(6), 3))
