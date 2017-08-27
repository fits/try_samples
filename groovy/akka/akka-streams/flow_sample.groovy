@Grab('com.typesafe.akka:akka-stream_2.12:2.5.4')
import akka.actor.*
import akka.stream.*
import akka.stream.javadsl.*

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def flow = Flow.fromSinkAndSource(
	Sink.foreach { println "A - ${it}" },
	Source.from(1..10).filter { it % 2 == 0}
)

def res = flow.runWith(
	Source.from(11..20),
	Sink.foreach { println "B - ${it}" },
	mat
)

println res

sleep 1000

system.terminate()
