@Grab('com.typesafe.akka:akka-stream_2.12:2.5.4')
import akka.actor.*
import akka.stream.*
import akka.stream.javadsl.*

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def src = Source.from(1..10)

def res = src.runWith(Sink.fold(0) { acc, v -> acc + v }, mat)

println res.toCompletableFuture().get()

println '----------'

src.filter { it % 2 == 0 }.to(Sink.foreach { println it }).run(mat)

src.via(Flow.create().grouped(3)).to(Sink.foreach { println it }).run(mat)

sleep 1000

system.terminate()
