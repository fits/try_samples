@Grab('com.typesafe.akka:akka-stream_2.12:2.5.4')
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.javadsl.Source
import akka.stream.javadsl.Sink

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def stream = Source.tick(
	Duration.create(0, TimeUnit.SECONDS), 
	Duration.create(1, TimeUnit.SECONDS),
	10
).groupedWithin(5, Duration.create(3, TimeUnit.SECONDS))

stream.runWith(Sink.foreach { println it }, mat)

sleep 10000

system.terminate()
