@Grab('com.typesafe.akka:akka-remote_2.12:2.5.4')
import akka.actor.*
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class SampleListener extends AbstractActor {
	AbstractActor.Receive createReceive() {
		receiveBuilder()
			.matchAny {
				println "*** receive from server: ${it}"
			}
			.build()
	}
}

class ClientActor extends AbstractActor {
	def server
	def listener

	void preStart() {
		server = getContext().actorSelection('akka.tcp://sampleServer@127.0.0.1:2552/user/serverActor')

		listener = getContext().actorOf(Props.create(SampleListener))
	}

	AbstractActor.Receive createReceive() {
		receiveBuilder()
			.matchAny {
				println it
				server.tell(it, listener)
			}
			.build()
	}
}

def system = ActorSystem.create('clientSample', ConfigFactory.load('client'))

def actor = system.actorOf(Props.create(ClientActor))

(1..5).each {
	actor.tell(LocalDateTime.now().toString(), null)
	sleep 1000
}

Await.ready(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
