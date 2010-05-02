
class UserFeature {
	def name

	def test(msg) {
		"$name - $msg"
	}
}

class User {

	@Delegate UserFeature feature = new UserFeature()

	void printState() {
		println name
	}
}

def u = new User()
u.feature.name = "test"

u.printState()

println u.test("abc")
println "${u.name}"

println "--- methods ---"
println User.metaClass.methods
