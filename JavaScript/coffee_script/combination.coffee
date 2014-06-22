
flatten = (xs) -> xs.reduce (x, y) -> x.concat y

combination = (xs) -> flatten( [x, y] for x in xs when x != y for y in xs )

list = ["A", "B", "C"]

console.log combination(list)
