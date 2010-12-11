
@Immutable class User {
	String name
}

//Ruby•—
@Newify createUser(arg) {
	User.new(name: arg)
}

//Python•—
@Newify([User]) createUser2(arg) {
	User(name: arg)
}

def u1 = createUser("test")
println u1.dump()

def u2 = createUser2("test")
println u2.dump()

println u1 == u2
