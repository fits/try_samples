package fits.sample

import akka.actor._

case class Hello(message: String)
case class Hi(message: String)

class SampleActor extends Actor {
	def receive = {
		case he: Hello => log.info("*** received Hello : " + he.message)
		case hi: Hi => log.info("*** received Hi : " + hi.message)
		case _ => {
			log.info("*** received others")
			//reply
			self.reply("return")
		}
	}
}
