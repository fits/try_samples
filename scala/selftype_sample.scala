
class A { self =>
	def test(msg: String) = println("A_test:" + msg)

	object B {
		object C {
			//自分型 self を使って A の test メソッドを呼び出す
			def test(msg: String) = self.test("C_test:" + msg)
		}
	}
}

val a = new A
a.B.C.test("abc")

println("---------------------")

trait A1 {
	def a1 = println("a1")
}

trait A2 {
	def a2 = println("a2")
}

/**
 * As の自分型 が A1 と A2 のトレイトであることを指定
 */
trait As { self: A1 with A2 =>
	def a = {
		a1
		a2
	}
}

// As をミックスインするには A1 と A2 もミックスインしなければならない
object Sample extends As with A1 with A2

Sample.a
