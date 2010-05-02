
import scala.actors.Actor._

val a = actor {
	while (true) {
		receive {
			case s: String => println("string: " + s)
			case zero if zero == 0 => println("zero")
			case n => println("other: " + n)
		}
	}
}

a ! 100
a ! "test"
a ! "a1"
a ! 0
