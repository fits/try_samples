@Grab('org.apache.kafka:kafka-clients:0.10.2.0')
@Grab('org.slf4j:slf4j-simple:1.7.24')
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

def props = new Properties()

props.setProperty('bootstrap.servers', 'localhost:9092')
props.setProperty('group.id', 'test')
props.setProperty('key.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')
props.setProperty('value.deserializer', 'org.apache.kafka.common.serialization.StringDeserializer')

def stopLatch = new CountDownLatch(1)

def es = Executors.newSingleThreadExecutor()

es.submit {
	new KafkaConsumer(props).withCloseable { consumer ->

		consumer.subscribe(['sample'])

		while(stopLatch.count > 0) {
			def records = consumer.poll(1000)

			records.each {
				println "***** result: ${it}"
			}
		}
	}
}

System.in.read()

stopLatch.countDown()

es.shutdown()
