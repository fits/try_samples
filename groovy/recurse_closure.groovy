def sample = { num ->
	(num > 0)? num + call(num - 1): num
}

println sample(args[0] as int)
