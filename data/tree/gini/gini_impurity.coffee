
countBy = (xs) -> xs.reduce (acc, x) ->
	acc[x] ?= 0
	acc[x]++
	acc
, {}

sum = (xs) -> xs.reduce (acc, x) -> acc + x

gini1 = (xs) -> 1 - sum( (v / xs.length) ** 2 for k, v of countBy(xs) )


list = ["A", "B", "B", "C", "B", "A"]

console.log countBy(list)
console.log gini1(list)

