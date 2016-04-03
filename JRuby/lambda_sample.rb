
f = ->(x) {
	puts "call lambda #{x}"
	x + 10
}

puts f.call(5)
