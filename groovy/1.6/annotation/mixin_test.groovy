
class UserFeature {
	def name
	def test(msg) {
		"$name - $msg"
	}
}

@Mixin(UserFeature)
class User {

	void printState() {
		println name
	}
}

def u = new User(name: "test")

u.printState()

println u.test("abc")

println "--- methods ---"
println User.metaClass.methods
println "--- properties ---"
println User.metaClass.properties
