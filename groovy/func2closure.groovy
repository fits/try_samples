def sample(num) {
	(num > 0)? num + sample(num - 1): num
}

println sample(args[0] as int)

def sample1 = this.&sample

println sample1.class
