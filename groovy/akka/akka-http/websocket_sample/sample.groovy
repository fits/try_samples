@Grab('com.typesafe.akka:akka-http_2.12:10.0.9')
import akka.japi.JavaPartialFunction
import akka.actor.*
import akka.http.javadsl.*
import akka.http.javadsl.server.AllDirectives
import akka.http.javadsl.model.ws.*
import akka.stream.ActorMaterializer
import akka.stream.javadsl.Flow

class Util {
	static def websocketFlow(String name) {
		Flow.create().collect(new JavaPartialFunction<Message, Message>() {
			Message apply(Message msg, boolean isCheck) {
				println "*** receive ${msg}, ${isCheck}"

				if (isCheck) return null

				if (msg.isStrict()) {
					TextMessage.create("${name}: ${msg.strictText}")
				}
				else {
					throw noMatch()
				}
			}
		})
	}
}

class SampleRoute extends AllDirectives {
	def createRoute() {
		route(
			get {
				pathSingleSlash {
					getFromResource('index.html')
				}
			},
			get {
				path('sample') {
					parameter('name') { name -> 
						handleWebSocketMessages(Util.websocketFlow(name))
					}
				}
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
