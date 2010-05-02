
class ApplyTest {
	def apply(i: Int) = i * 2
	def apply(s: String) = s + "_test"
}

val a = new ApplyTest()
println(a(10))
println(a("test"))

println(a apply 100)
println(a apply "a")

