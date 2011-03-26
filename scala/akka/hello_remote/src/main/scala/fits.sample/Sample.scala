package fits.sample

import akka.actor._

object SampleServer {
	def run() = {
		Actor.remote.start("localhost", 8088)
		Actor.remote.register("sample", Actor.actorOf[SampleActor])
	}
}

object SampleClient {
	def run() = {
		val sactor = Actor.remote.actorFor("sample", "localhost", 8088)

		sactor ! Hello("test")
		sactor ! Hi("a")

		sactor !! "aaa" match {
			case Some(reply) => println("-------- reply : " + reply)
			case None => println("-------- timeout")
		}
	}
}

object Sample {
	def main(args: Array[String]) = SampleServer.run
}
