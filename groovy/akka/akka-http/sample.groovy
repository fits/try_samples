@Grab('com.typesafe.akka:akka-http_2.12:10.0.10')
import akka.actor.*
import akka.http.javadsl.*
import akka.http.javadsl.server.AllDirectives
import akka.stream.ActorMaterializer

class SampleRoute extends AllDirectives {
	def createRoute() {
		route(
			path('sample') {
				complete('sample')
			}
		)
	}
}

def system = ActorSystem.create("sample")
def mat = ActorMaterializer.create(system)

def http = Http.get(system)

def routeFlow = new SampleRoute().createRoute().flow(system, mat)

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
