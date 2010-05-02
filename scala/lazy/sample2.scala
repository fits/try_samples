
def test(b: Boolean, x: Any, y: Any) = {
	val res = b match {
		case true => x
		case _ =>  y
	}
	res
}

val res = test(1 == 1, true, false)
println("result: " + res)

println("result2:" + test(1 < 0, "a", "b"))

// x と y を遅延評価で定義するため
// 実際に使われる際に評価が実施される。
// そのため b が true の場合は x だけが評価され y は評価されない
def test2(b: Boolean, x: => Int, y: => Int) = {
	b match {
		case true => x
		case _ =>  y
	}
}


def a(n: Int): Int = {
	test2((n == 0), 1, (n * a(n - 1)))
}

println(a(5))
