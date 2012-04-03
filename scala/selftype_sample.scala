
class A { self =>
	def test(msg: String) = println("A_test:" + msg)

	object B {
		object C {
			//自分型アノテーション self を使って A の test メソッドを呼び出す
			def test(msg: String) = self.test("C_test:" + msg)
		}
	}
}

val a = new A
a.B.C.test("abc")

