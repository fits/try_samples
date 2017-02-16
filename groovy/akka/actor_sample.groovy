@Grab('com.typesafe.akka:akka-actor_2.12:2.5-M1')
import akka.actor.*
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SampleActor extends UntypedActor {
	void onReceive(msg) {
		println "*** receive: ${msg}"
	}
}

def system = ActorSystem.create('sample')
def actor = system.actorOf(Props.create(SampleActor))

actor.tell('sample message', ActorRef.noSender())

sleep 1000

Await.result(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
