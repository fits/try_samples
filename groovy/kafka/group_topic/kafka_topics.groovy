@Grab('org.apache.kafka:kafka-clients:0.10.2.0')
@Grab('org.slf4j:slf4j-nop:1.7.24')
import org.apache.kafka.clients.consumer.KafkaConsumer

def props = new Properties()

props.setProperty('bootstrap.servers', 'localhost:9092')
props.setProperty('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
props.setProperty('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')

new KafkaConsumer(props).withCloseable { consumer ->
	consumer.listTopics().each { k, v ->
		println "${k}"
		// println "${k} : ${v}"
	}
}
