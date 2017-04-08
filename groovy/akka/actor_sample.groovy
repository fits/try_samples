@Grab('com.typesafe.akka:akka-actor_2.12:2.5.0-RC2')
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.AbstractActor
import akka.actor.AbstractActor.Receive
import akka.actor.Props

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SampleActor extends AbstractActor {
	Receive createReceive() {
		receiveBuilder()
			.match(String) { msg ->
				println "*** receive: ${msg}"
			}
			.build()
	}
}

def system = ActorSystem.create('sample')
def actor = system.actorOf(Props.create(SampleActor))

actor.tell('sample message', ActorRef.noSender())

sleep 1000

Await.result(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
