
flatten = (xs) -> xs.reduce (x, y) -> x.concat y

easyCombination = (xs) -> flatten( [x, y] for x in xs when x isnt y for y in xs )

easyCombination2 = (xs) -> flatten( [x, y] for x, i in xs when i isnt j for y, j in xs )

combination = (n, xs) ->
	if n is 0
		[[]]
	else if not xs? or xs.length < n
		[]
	else
		( [xs[0]].concat y for y in combination(n - 1, xs[1..]) ).concat(combination(n, xs[1..]))


list = ["A", "B", "C"]

console.log easyCombination(list)
console.log easyCombination2(list)

console.log combination(1, list)
console.log combination(2, list)
console.log combination(3, list)
console.log combination(4, list)
console.log combination(2, null)

console.log combination(2, ["A", "B", "C", "D"])
console.log combination(3, ["A", "B", "C", "D"])

