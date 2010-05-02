import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.jcl.Conversions._


def printThread(actor: Actor, msg: String) = {
	println(Thread.currentThread() + " " + msg + " - " + actor)
}

val consumer = actor {
	loop {
		react {
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
		react {
			case r: int => {
				printThread(self, "return : " + r)
			}
		}
	}
}

val producer2 = actor {
	consumer ! "abc"

	react {
		case r: int => {
			printThread(self, "return : " + r)
		}
	}
}


//printThread(null, "main")

//implicit conversion ‚Å java.util.Set ‚ğ•ÏŠ·
Thread.getAllStackTraces().keySet() foreach {
	thread => println("\t" + thread)
}

