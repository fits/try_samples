
import scala.io.Source

case class Header(d: String)

val datePattern = """([0-9]{2} [0-9]{2}, [0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}) .*""".r

val res = Source.fromFile(args(0), "Shift_JIS").getLines.foldLeft(List[(Header, String)]()) {(b, a) =>
	a match {
		case datePattern(d) => (Header(d), "") :: b
		case c => b match {
			case (h, s) :: xs => (h, s + "\n" + c) :: xs
			case _ => b
		}
	}
}

println(res)

