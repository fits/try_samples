package fits.sample

import akka.actor._
import akka.util.Logging

object SampleServer {
	def run() = {
		Actor.remote.start("localhost", 8088)
		Actor.remote.register("sample", Actor.actorOf[SampleActor])
	}
}

object SampleClient extends Logging {
	def run() = {
		val sactor = Actor.remote.actorFor("sample", "localhost", 8088)

		sactor ! Hello("test")
		sactor ! Hi("a")

		sactor !! "aaa" match {
			case Some(reply) => log.info("-------- reply : " + reply)
			case None => log.info("-------- timeout")
		}
	}
}

object Sample {
	def main(args: Array[String]) = SampleServer.run
}
