import scala.actors.Actor
import scala.actors.Actor._

def printThread(actor: Actor, msg: String) = {
	println(Thread.currentThread() + " " + msg + " - " + actor)
}

val consumer = actor {
	loop {
		receive {
			case msg: String if msg == "test" => {
				printThread(self, msg)
				reply(0)
			}
			case msg: String => {
				printThread(self, msg)
				reply(1)
			}
		}
	}
}

val producer1 = actor {
	consumer ! "test"
	consumer ! "check"

	loop {
		receive {
			case r: int => {
				printThread(self, "return : " + r)
			}
		}
	}
}

val producer2 = actor {
	consumer !? "abc" match {
		case r: int => {
			printThread(self, "return : " + r)
		}
	}
}


printThread(null, "main")

