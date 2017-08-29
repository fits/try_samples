@Grab('com.typesafe.akka:akka-stream_2.12:2.5.4')
import akka.actor.*
import akka.stream.*
import akka.stream.javadsl.*

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class Publisher extends AbstractActor {

	String name

	Publisher(String name) {
		this.name = name
	}

	AbstractActor.Receive createReceive() {
		receiveBuilder()
			.match(String) { msg ->
				println "*** receive: ${msg}, ${name}"
				context().system().eventStream().publish("${name}: ${msg}".toString())
			}
			.build()
	}
}

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def actor = system.actorOf(Props.create(Publisher, 'pub1'))

def flow = Flow.fromSinkAndSource(
	Sink.actorRef(actor, 'end'),
	Source.actorRef(1, OverflowStrategy.fail()).mapMaterializedValue {
		system.eventStream().subscribe(it, String)
	}
)

def res = flow.runWith(
	Source.from(0..10).map {
		"num-${it}".toString()
	},
	Sink.foreach { println "--- ${it}" },
	mat
)

sleep 1000

system.terminate()
