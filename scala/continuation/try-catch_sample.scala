import scala.util.continuations._

reset {
	try{
		val r = shift {k: (String => Unit) =>
			k("test")
			println("(3)")
		}

		println("(1) : " + r)

		//{・・・}: Unit としないと warning が発生
		{
			if (r == "test") {
				throw new Exception(r)
			}
		}: Unit

	}
	catch {
		case e => {
			println("(2)")
			println("error: " + e)
		}
	}
}
