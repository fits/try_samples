
import scala.actors.Actor._

val consumer = actor {
	loop {
		receive {
			case msg: String if msg == "test" => {
				println("receive test " + Thread.currentThread())
				reply(0)
			}
			case msg: String => {
				println("receive: " + msg + ", " + Thread.currentThread())
				reply(1)
			}
		}
	}
}

val producer = actor {
	consumer ! "test"
	consumer ! "abc"

	loop {
		receive {
			case r: int => println("return : " + r + ", " + Thread.currentThread())
		}
	}
}


