@Grab('com.typesafe.akka:akka-stream_2.12:2.5.4')
import akka.actor.*
import akka.stream.*
import akka.stream.javadsl.*

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def src = Source.from(1..10)

def res = src.runWith(Sink.fold(0) { acc, v -> acc + v }, mat)

println res.toCompletableFuture().get()

sleep 1000

system.terminate()
