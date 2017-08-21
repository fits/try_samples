@Grab('com.typesafe.akka:akka-remote_2.12:2.5.4')
import akka.actor.*
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class ServerActor extends AbstractActor {
	AbstractActor.Receive createReceive() {
		receiveBuilder()
			.matchAny {
				println "*** receive: ${it}"
				sender().tell('ok', self())
			}
			.build()
	}
}

def system = ActorSystem.create('sampleServer', ConfigFactory.load('server'))

def actor = system.actorOf(Props.create(ServerActor), 'serverActor')

System.in.read()

Await.ready(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
