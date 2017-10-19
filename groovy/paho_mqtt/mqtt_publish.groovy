@Grab('org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.0')
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage

def topic = args[0]
def payload = args[1]

def brokerUri = 'tcp://localhost:1883'

def client = new MqttClient(brokerUri, MqttClient.generateClientId())

client.connect()

def msg = new MqttMessage(payload.getBytes('UTF-8'))

client.publish(topic, msg)

client.disconnect()
client.close()
