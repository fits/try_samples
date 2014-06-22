
countBy = (xs) -> xs.reduce (acc, x) ->
	acc[x] ?= 0
	acc[x]++
	acc
, {}

sum = (xs) -> xs.reduce (acc, x) -> acc + x

gini1 = (xs) -> 1 - sum( (v / xs.length) ** 2 for k, v of countBy(xs) )

flatten = (xs) -> xs.reduce (x, y) -> x.concat y

combination = (xs) -> flatten( [x, y] for x in xs when x isnt y for y in xs )

gini2 = (xs) -> sum( (x[1] / xs.length) * (y[1] / xs.length) for [x, y] in combination([k, v] for k, v of countBy(xs)) )


list = ["A", "B", "B", "C", "B", "A"]

console.log gini1(list)
console.log gini2(list)
