@Grab('com.typesafe.akka:akka-http_2.12:10.0.10')
import static akka.http.javadsl.server.Directives.*
import akka.actor.ActorSystem
import akka.http.javadsl.Http
import akka.http.javadsl.ConnectHttp
import akka.stream.ActorMaterializer

def system = ActorSystem.create('sample')
def mat = ActorMaterializer.create(system)

def http = Http.get(system)

def createRoute = {
	route(
		path('data') {
			parameter('v') { v -> 
				println v
				complete('ok')
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
