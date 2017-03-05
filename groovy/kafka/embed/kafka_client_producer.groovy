@Grab('org.apache.kafka:kafka-clients:0.10.2.0')
@Grab('org.slf4j:slf4j-simple:1.7.24')
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

def props = new Properties()

props.setProperty('bootstrap.servers', 'localhost:9092')
props.setProperty('key.serializer', 'org.apache.kafka.common.serialization.StringSerializer')
props.setProperty('value.serializer', 'org.apache.kafka.common.serialization.StringSerializer')

new KafkaProducer(props).withCloseable { producer ->

	def res = producer.send(new ProducerRecord('sample', 'msg', args[0]))

	println "***** result: ${res.get()}"
}
