class Test(var name: String, point: int) {

	var address = "東京都"
	def tel = "1111"

	def printSelf() = {
		println("name : " + name + ", point: " + point + ", address: " + address + ", tel: " + tel)
	}

	def print = {
		println("name=" + name)
	}
}

var t = new Test("aaa", 100)
t.printSelf()

t.name = "bbbb"
t.address = "大阪"

//def 宣言されているとメンバーフィールドとしてアクセス不可
//t.tel = "123"
println(t.tel)

t.printSelf()
t.print

t = null

val t2 = new Test("aaa", 100)
t2.printSelf()

//以下を実行するとエラーが発生
//t2 = null

