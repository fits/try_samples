@Grab('org.apache.kafka:kafka-clients:0.11.0.1')
@Grab('org.slf4j:slf4j-simple:1.7.24')
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

def topic = args[0]
def key = args[1]
def value = args[2]

def props = new Properties()

props.setProperty('bootstrap.servers', 'localhost:9092')
props.setProperty('key.serializer', 'org.apache.kafka.common.serialization.StringSerializer')
props.setProperty('value.serializer', 'org.apache.kafka.common.serialization.StringSerializer')

new KafkaProducer(props).withCloseable { producer ->

	def res = producer.send(new ProducerRecord(topic, key, value))

	println "***** result: ${res.get()}"
}
