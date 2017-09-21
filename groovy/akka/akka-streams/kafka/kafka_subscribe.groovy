@Grab('com.typesafe.akka:akka-stream-kafka_2.12:0.17')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.javadsl.Sink
import akka.kafka.Subscriptions
import akka.kafka.ConsumerSettings
import akka.kafka.javadsl.Consumer
import org.apache.kafka.common.serialization.StringDeserializer

def topic = args[0]
def groupId = args[1]

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def settings = ConsumerSettings.create(
	system,
	new StringDeserializer(),
	new StringDeserializer()
)
.withBootstrapServers('localhost:9092')
.withGroupId(groupId)

Consumer.atMostOnceSource(settings, Subscriptions.topics(topic))
	.runWith(Sink.foreach { println "*** $it" }, mat)

println "subscribe : ${topic}"

System.in.read()

system.terminate()
