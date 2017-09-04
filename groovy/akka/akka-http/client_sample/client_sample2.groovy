@Grab('com.typesafe.akka:akka-http_2.12:10.0.10')
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.javadsl.Source
import akka.stream.javadsl.Sink
import akka.http.javadsl.Http
import akka.http.javadsl.model.HttpRequest

def value = args[0]

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def res = Source.single("http://localhost:8080/data?v=${value}")
	.mapAsync(1) {
		Http.get(system).singleRequest(HttpRequest.POST(it), mat)
	}
	.runWith(Sink.foreach { println it }, mat)


println "*** runWith result: ${res.get()}"

system.terminate()
