@Grab('com.typesafe.akka:akka-stream_2.12:2.5.4')
@Grab('com.lightbend.akka:akka-stream-alpakka-mqtt_2.12:0.11')
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.javadsl.Source
import akka.stream.alpakka.mqtt.MqttQoS
import akka.stream.alpakka.mqtt.MqttConnectionSettings
import akka.stream.alpakka.mqtt.MqttMessage
import akka.stream.alpakka.mqtt.javadsl.MqttSink
import akka.util.ByteString

def topic = args[0]
def clientId = args[1]
def message = args[2]

def system = ActorSystem.create()
def mat = ActorMaterializer.create(system)

def settings = MqttConnectionSettings.create(
	'tcp://localhost:1883',
	clientId,
	new org.eclipse.paho.client.mqttv3.persist.MemoryPersistence()
)

def msg = MqttMessage.create(topic, ByteString.fromString(message))

Source.single(msg)
	.runWith(MqttSink.create(settings, MqttQoS.atLeastOnce()), mat)

sleep 1000

system.terminate()
