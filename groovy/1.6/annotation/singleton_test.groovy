
@Singleton(lazy = true) class LazySingle {
	def name = "abc"
	def test(msg) {
		"$name - $msg"
	}
}

println LazySingle.instance.test("test")
