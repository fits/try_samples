@Grab('com.typesafe.akka:akka-http_2.12:10.0.9')
@Grab('com.typesafe.akka:akka-stream_2.12:2.5.4')
import static akka.http.javadsl.server.Directives.*
import akka.NotUsed
import akka.actor.*
import akka.actor.AbstractActor.Receive
import akka.http.javadsl.Http
import akka.http.javadsl.ConnectHttp
import akka.http.javadsl.model.ws.*
import akka.stream.ActorMaterializer
import akka.stream.OverflowStrategy
import akka.stream.javadsl.Source
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Flow

class Ready {}
class Done {}

class SampleActor extends AbstractActor {
	String name
	ActorRef subscriber

	SampleActor(String name) {
		this.name = name
	}

	void postStop() {
		println "*** post stop: ${name}"
	}

	Receive createReceive() {
		receiveBuilder()
			.match(Ready) { msg ->
				println "*** ready - ${name}: ${msg}"

				context().watch(sender())
				subscriber = sender()
				context().system().eventStream().subscribe(subscriber, String)
			}
			.match(Done) {
				println "*** complete - ${name}, ${it}"

				context().system().eventStream().unsubscribe(subscriber)
				context().stop(self())
			}
			.match(Terminated) {
				println "*** terminated - ${name}, ${it}"

				context().system().eventStream().unsubscribe(subscriber)
				context().stop(self())
			}
			.match(String) { msg ->
				println "*** received - ${name}: ${msg}"
				context().system().eventStream().publish("${name}: ${msg}".toString())
			}
			.matchAny { msg ->
				println "*** no match: ${msg}"
			}
			.build()
	}
}

class Util {
	static def websocketFlow(ActorSystem system, String name) {
		def actor = system.actorOf(Props.create(SampleActor, name))

		def sink = Flow.create().map {
			println "*** ws : ${it}"
			it.strictText
		}.to( Sink.actorRef(actor, new Done()) )

		def source = Source.actorRef(5, OverflowStrategy.fail()).mapMaterializedValue {
			actor.tell(new Ready(), it)
			NotUsed.instance
		}.map {
			TextMessage.create(it)
		}

		Flow.fromSinkAndSource(sink, source)
	}
}


def system = ActorSystem.create("sample")
def mat = ActorMaterializer.create(system)

def http = Http.get(system)

def createRoute = {
	route(
		get {
			pathSingleSlash {
				getFromResource('index.html')
			}
		},
		get {
			path('sample') {
				parameter('name') { name -> 
					handleWebSocketMessages(
						Util.websocketFlow(system, name)
					)
				}
			}
		}
	)
}

def routeFlow = createRoute().flow(system, mat)

def binding = http.bindAndHandle(
	routeFlow, 
	ConnectHttp.toHost('localhost', 8080),
	mat
)

println 'start ...'

System.in.read()

binding.thenCompose {
	it.unbind()
}.thenAccept {
	system.terminate()
}
