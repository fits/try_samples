
def sum(args: Int*) = {
	println(args.elements)

	var result = 0
	for (i <- args.elements) result += i
	result
}

println(sum())
println(sum(1, 2))
