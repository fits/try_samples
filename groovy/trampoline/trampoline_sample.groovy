
def calc = { num ->
	println num

	num < 5 ? null: trampoline(num - 1)
}.trampoline()

calc(10)
