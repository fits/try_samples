@Grab('org.apache.pulsar:pulsar-client:2.4.1')
import org.apache.pulsar.client.api.*
import static java.nio.charset.StandardCharsets.*

def topicName = args[0]
def msg = args[1]

def client = PulsarClient.builder().serviceUrl('pulsar://localhost:6650').build()

client.withCloseable {
    def producer = client.newProducer().topic(topicName).create()

    producer.withCloseable {
        def id = producer.send(msg.getBytes(UTF_8))

        println "message id: $id"
    }
}