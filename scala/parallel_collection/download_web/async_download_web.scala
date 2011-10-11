import scala.util.continuations._
import scala.actors.Actor
import scala.actors.Actor._

import java.io.InputStream
import java.net.URL

class URLActor(k: (InputStream => Unit)) extends Actor {
	def act() {
		loop {
			react {
				case url: String => {
					println("receive : " + url)
					k(new URL(url).openStream())
					//exit を実施しないと終了しない
					exit()
				}
			}
		}
	}
}

val url = args(0)

reset {
	val stream = shift {k: (InputStream => Unit) =>
		val actor = new URLActor(k)
		actor.start
		actor ! url

		println("actor ! " + url)
	}

	

	println("stream = " + stream)
}
