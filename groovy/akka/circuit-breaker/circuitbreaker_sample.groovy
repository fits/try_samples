@Grab('com.typesafe.akka:akka-actor_2.12:2.5.0-RC2')
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.AbstractActor
import akka.actor.AbstractActor.Receive
import akka.actor.Props
import akka.pattern.CircuitBreaker
import akka.pattern.Patterns

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SampleActor extends AbstractActor {

	def breaker = CircuitBreaker.create(
		context.system().scheduler(),
		2,
		Duration.create(1, TimeUnit.SECONDS),
		Duration.create(3, TimeUnit.SECONDS),
	).onOpen {
		println '*** CircuitBreaker opened'
	}
	.onClose {
		println '*** CircuitBreaker closed'
	}
	.onHalfOpen {
		println '*** CircuitBreaker halfOpened'
	}

	Receive createReceive() {
		receiveBuilder()
			.match(String) { msg ->

				def res = ''
				def sleepTime = msg.startsWith('heavy') ? 1200 : 10

				try {
					res = breaker.callWithSyncCircuitBreaker {
						sleep sleepTime
						"reply< ${msg} >".toString()
					}
				} catch (e) {
					println e
					res = "breaker< $msg >"
				}

				getSender().tell(res, getSelf())
			}
			.build()
	}
}

def system = ActorSystem.create('sample')
def actor = system.actorOf(Props.create(SampleActor))

def ask = { String msg ->
	Await.result(
		Patterns.ask(actor, msg, 2000),
		Duration.create(10, TimeUnit.SECONDS)
	)
}

(0..<10).each {
	println ask("light-${it}")
}

(0..<5).each {
	println ask("heavy-${it}")
}

sleep 5000

println ask('light-a')
println ask('heavy-a')

Await.result(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
