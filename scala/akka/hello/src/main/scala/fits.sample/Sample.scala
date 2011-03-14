package fits.sample

import akka.actor.Actor

object Sample {
	def main(args: Array[String]) {

		val sactor = Actor.actorOf[SampleActor].start

		sactor ! "test"
		sactor ! "hello"

		sactor.stop
	}
}
