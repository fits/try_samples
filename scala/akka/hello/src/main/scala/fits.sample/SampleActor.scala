package fits.sample

import akka.actor._

class SampleActor extends Actor {
	def receive = {
		case "hello" => log.info("*** received hello")
		case _ => log.info("*** received others")
	}
}
