@Grab('com.typesafe.akka:akka-actor_2.12:2.5-M1')
import akka.actor.*
import akka.routing.FromConfig

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class Worker extends UntypedActor {
	void preStart() {
		println "[Worker] preStart: ${this}"
	}

	void postStop() {
		println "[Worker] postStop: ${this}"
	}

	void onReceive(msg) {
		println "[Worker] receive: ${this}, ${msg}"
	}
}

class Manager extends UntypedActor {

	def router = getContext().actorOf(
		FromConfig.getInstance().props(Props.create(Worker)), 
		'router1'
	)

	void preStart() {
		println "[Manager] preStart: ${this}"
	}

	void postStop() {
		println "[Manager] postStop: ${this}"
	}

	void onReceive(msg) {
		println "[Manager] receive: ${this}, ${msg}"

		router.tell(msg, getSelf())
	}
}

def system = ActorSystem.create('sample')

def actor = system.actorOf(Props.create(Manager), 'manager')

(0..<10).each {
	actor.tell("message${it}", ActorRef.noSender())
}

sleep 1000

Await.result(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
