package fits.sample

import akka.actor._

object Sample {
	def main(args: Array[String]) {

		val sactor = Actor.actorOf[SampleActor].start

		sactor ! Hello("test")
		sactor ! Hi("a")

		sactor !! "aaa" match {
			case Some(reply) => println("-------- reply : " + reply)
			case None => println("-------- timeout")
		}

		sactor.stop
	}
}
