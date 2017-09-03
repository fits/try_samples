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

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

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
	Pair.create(topic, MqttQoS.atMostOnce())
)

def mqttSrc = MqttSource.create(settings, 10)

def stream = mqttSrc.groupedWithin(5, Duration.create(10, TimeUnit.SECONDS))
					.map {
						it*.payload*.utf8String()*.length().max()
					}
					.filter { it >= 5 }

stream.runWith(Sink.foreach { println "max string length: ${it}" }, mat)

println "subscribe : ${topic}"

System.in.read()

system.terminate()
