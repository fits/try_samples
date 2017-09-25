@Grab('com.typesafe.akka:akka-stream_2.12:2.5.4')
@Grab('com.lightbend.akka:akka-stream-alpakka-mqtt_2.12:0.11')
import akka.actor.ActorSystem
import akka.japi.Pair
import akka.stream.ActorMaterializer
import akka.stream.javadsl.Sink
import akka.stream.alpakka.mqtt.MqttQoS
import akka.stream.alpakka.mqtt.MqttSourceSettings
import akka.stream.alpakka.mqtt.MqttConnectionSettings
import akka.stream.alpakka.mqtt.javadsl.MqttSource

def topic = args[0]
def clientId = args[1]

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def settings = MqttSourceSettings.create(
	MqttConnectionSettings.create(
		'tcp://localhost:1883',
		clientId,
		new org.eclipse.paho.client.mqttv3.persist.MemoryPersistence()
	)
).withSubscriptions(
	Pair.create(topic, MqttQoS.atLeastOnce())
)

MqttSource.create(settings, 10).runWith(Sink.foreach { println it }, mat)

println "subscribe : ${topic}"

System.in.read()

system.terminate()
