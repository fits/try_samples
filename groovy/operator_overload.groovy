
class Sample {
	def value

	def plus(v) {
		new Sample(value: value + v.value)
	}

	def leftShift(v) {
		value *= v.value
	}
}

def a1 = new Sample(value: 10)
def a2 = new Sample(value: 5)

def a3 = a1 + a2

println a3.value

a1 << a2

println a1.value
