
countBy = (xs) -> xs.reduce (acc, x) ->
	acc[x] ?= 0
	acc[x]++
	acc
, {}


list = ["A", "B", "B", "C", "B", "A"]

console.log countBy(list)
