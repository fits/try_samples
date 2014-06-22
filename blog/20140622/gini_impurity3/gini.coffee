
countBy = (xs) -> xs.reduce (acc, x) ->
	acc[x] ?= 0
	acc[x]++
	acc
, {}

sum = (xs) -> xs.reduce (acc, x) -> acc + x

# (a) 1 - (AA + BB + CC)
giniA = (xs) -> 1 - sum( (v / xs.length) ** 2 for k, v of countBy(xs) )

flatten = (xs) -> xs.reduce (x, y) -> x.concat y

combination = (xs) -> flatten( [x, y] for x in xs when x isnt y for y in xs )

# (b) BA + CA + AB + CB + AC + BC
giniB = (xs) -> sum( (x[1] / xs.length) * (y[1] / xs.length) for [x, y] in combination([k, v] for k, v of countBy(xs)) )


list = ["A", "B", "B", "C", "B", "A"]

console.log giniA(list)
console.log giniB(list)
