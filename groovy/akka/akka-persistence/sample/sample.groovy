@Grab('com.typesafe.akka:akka-persistence_2.12:2.5.0-RC1')
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import akka.actor.AbstractActor.Receive
import akka.pattern.Patterns

import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import java.util.concurrent.TimeUnit

import groovy.transform.Immutable

@Immutable
class CounterAdd implements Serializable {
	int count
}

@Immutable
class AddedCounter implements Serializable {
	int count
}

class CounterActor extends AbstractPersistentActor {
	private String id
	private int state = 0

	CounterActor(id) {
		this.id = id
	}

	String persistenceId() {
		id
	}

	Receive createReceive() {
		receiveBuilder()
			.match(CounterAdd) { cmd -> 
				persist(new AddedCounter(cmd.count)) { ev ->
					updateState(ev)
					context().system().eventStream().publish(ev)
				}
			}
			.matchEquals('dump') { cmd -> 
				println(state)
			}
			.matchEquals('end') { cmd ->
				context().stop(self())
			}
			.build()
	}

	Receive createReceiveRecover() {
		receiveBuilder()
			.match(AddedCounter) { ev -> 
				updateState(ev)
			}
			.match(SnapshotOffer) { ss -> 
				state = ss.snapshot()
			}
			.build()
	}

	private void updateState(event) {
		state += event.count
	}
}

def systemName = 'sample1'

def system = ActorSystem.create(systemName)

def actor = system.actorOf(Props.create(CounterActor, 'd1'))

actor.tell('dump', ActorRef.noSender())

actor.tell(new CounterAdd(3), ActorRef.noSender())
actor.tell(new CounterAdd(5), ActorRef.noSender())

actor.tell('dump', ActorRef.noSender())

actor.tell(new CounterAdd(5), ActorRef.noSender())

actor.tell('dump', ActorRef.noSender())

Await.ready(
	Patterns
		.gracefulStop(actor, Duration.create(5, TimeUnit.SECONDS), 'end')
		.flatMap({ t -> system.terminate() }, system.dispatcher())
	, Duration.create(5, TimeUnit.SECONDS)
)
