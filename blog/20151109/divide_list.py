
from functools import reduce

def divide_list(xs, n):
	q = len(xs) // n
	m = len(xs) % n

	return reduce(
		lambda acc, i:
			(lambda fr = sum([ len(x) for x in acc ]):
				acc + [ xs[fr:(fr + q + (1 if i < m else 0))] ]
			)()
		,
		range(n),
		[]
	)

range_list = lambda n: list(range(n))

print(divide_list(range_list(8), 3))
print(divide_list(range_list(7), 3))
print(divide_list(range_list(6), 3))

print(divide_list(range_list(6), 6))
