@Grab('com.typesafe.akka:akka-http_2.12:10.0.9')
import static akka.http.javadsl.server.Directives.*
import akka.NotUsed
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.http.javadsl.Http
import akka.http.javadsl.ConnectHttp
import akka.stream.ActorMaterializer
import akka.stream.OverflowStrategy
import akka.stream.javadsl.Source
import akka.stream.javadsl.Sink

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def http = Http.get(system)

def createRoute = {

	Source.actorRef(5, OverflowStrategy.fail()).mapMaterializedValue {
		system.eventStream().subscribe(it, String)
		NotUsed.instance
	}
	.groupedWithin(10, Duration.create(5, TimeUnit.SECONDS))
	.runWith(Sink.foreach { println it }, mat)

	route(
		path('data') {
			parameter('v') { v ->
				system.eventStream().publish(v)
				complete('ok')
			}
		}
	)
}

def handler = createRoute().flow(system, mat)

def binding = http.bindAndHandle(
	handler, 
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
