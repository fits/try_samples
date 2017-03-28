@Grab('org.apache.kafka:kafka-clients:0.10.2.0')
@Grab('org.slf4j:slf4j-nop:1.7.24')
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition

def topic = args[0]
def group = args[1]

def props = new Properties()

props.setProperty('bootstrap.servers', 'localhost:9092')
props.setProperty('group.id', group)
props.setProperty('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
props.setProperty('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')

new KafkaConsumer(props).withCloseable { consumer ->

	consumer.partitionsFor(topic).each {
		def partition = it.partition()

		def res = consumer.endOffsets([new TopicPartition(topic, partition)])

		res.each { k, v ->
			println "topic = ${k.topic()}, partition = ${k.partition()}, offset = ${v}"
		}
	}
}
