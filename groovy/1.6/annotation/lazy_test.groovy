
class User {
	@Lazy name = "test"
}

def u = new User()

//この段階では name は null
println u.dump()

//ゲッターメソッド呼び出し（name 初期化）
println u.name

//name に値が入っている
println u.dump()
