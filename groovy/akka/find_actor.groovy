@Grab('com.typesafe.akka:akka-actor_2.12:2.5.4')
import akka.actor.*

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SampleActor extends AbstractActor {
	AbstractActor.Receive createReceive() {
		receiveBuilder()
			.match(String) { msg ->
				println "*** receive: ${msg}"
			}
			.build()
	}
}

def system = ActorSystem.create("sample")

def findActor = { name ->
	system.actorSelection(name)
		.resolveOneCS(Duration.create(5, TimeUnit.SECONDS))
		.toCompletableFuture()
		.thenAccept { println it }
		.exceptionally { println "*** ERROR ${name}: ${it}" }
}


def a1 = system.actorOf(Props.create(SampleActor), 'a1')

println a1

findActor '/user/a1'

findActor '/user/b2'

sleep 1000

system.terminate()
