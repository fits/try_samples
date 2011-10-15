import scala.util.continuations._

reset {
	val r = shift {k: (String => Unit) =>
		println("(1)")

		try {
			//以下の場合 (1) -> (2a) -> (3) の順になる
			k("test".substring(10, 12))

			//以下の場合 (1) -> (2b) -> (3) の順になる
			//k("test")

		} catch {
			case e => println("(2a) : " + e)
		}

		println("(3)")
	}

	println("(2b) : " + r)
}
