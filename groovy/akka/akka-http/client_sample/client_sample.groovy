@Grab('com.typesafe.akka:akka-http_2.12:10.0.9')
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.javadsl.Http
import akka.http.javadsl.model.HttpRequest

def value = args[0]

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def url = "http://localhost:8080/data?v=${value}"

def future = Http.get(system).singleRequest(HttpRequest.POST(url), mat)

println future.get()

system.terminate()
