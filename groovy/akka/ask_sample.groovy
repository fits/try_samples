@Grab('com.typesafe.akka:akka-actor_2.12:2.5.4')
import akka.actor.*
import akka.pattern.Patterns

import scala.compat.java8.FutureConverters

class Worker extends AbstractActor {
	AbstractActor.Receive createReceive() {
		receiveBuilder()
			.matchEquals('return') {
				sender().tell("ok", self())
			}
			.matchEquals('no-return') {
				// no return
			}
			.build()
	}
}

class Manager extends AbstractActor {
	def worker

	Manager(worker) {
		this.worker = worker
	}

	AbstractActor.Receive createReceive() {
		receiveBuilder()
			.match(String) {
				def res = Patterns.ask(worker, it, 500)

				println FutureConverters.toJava(res).exceptionally {
					println it
					'*** failed'
				}.get()
			}
			.build()
	}
}

def system = ActorSystem.create("sample")

def worker = system.actorOf(Props.create(Worker))
def manager = system.actorOf(Props.create(Manager, worker))

manager.tell('return', ActorRef.noSender())
manager.tell('no-return', ActorRef.noSender())

sleep 1000

system.terminate()
