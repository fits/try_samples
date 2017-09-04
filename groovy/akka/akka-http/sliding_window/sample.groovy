@Grab('com.typesafe.akka:akka-http_2.12:10.0.10')
import static akka.http.javadsl.server.Directives.*
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

	def actor = Source.actorRef(5, OverflowStrategy.fail())
					.groupedWithin(10, Duration.create(5, TimeUnit.SECONDS))
					.to(Sink.foreach { println it })
					.run(mat)

	route(
		path('data') {
			parameter('v') { v ->
				actor.tell(v, ActorRef.noSender())
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
