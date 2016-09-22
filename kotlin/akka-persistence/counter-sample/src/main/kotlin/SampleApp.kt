
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.Patterns

import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

import sample.*

fun main(args: Array<String>) {
	val system = ActorSystem.apply("sample1")
	val actor = system.actorOf(Props.create(SampleActor::class.java, "data1"))

	actor.tell("dump", ActorRef.noSender())

	actor.tell(CounterAdd(1), ActorRef.noSender())

	actor.tell("dump", ActorRef.noSender())

	actor.tell("snapshot", ActorRef.noSender())

	actor.tell(CounterAdd(3), ActorRef.noSender())

	actor.tell("dump", ActorRef.noSender())

	val timeout = FiniteDuration.apply(5, TimeUnit.SECONDS)

	Patterns.gracefulStop(actor, timeout, "end").onComplete(
		{ system.terminate() },
		system.dispatcher()
	)
}
