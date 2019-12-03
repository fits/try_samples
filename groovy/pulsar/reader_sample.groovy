@Grab('org.apache.pulsar:pulsar-client:2.4.1')
import org.apache.pulsar.client.api.*
import static java.nio.charset.StandardCharsets.*

def topicName = args[0]

def client = PulsarClient.builder().serviceUrl('pulsar://localhost:6650').build()

client.withCloseable {
    def reader = client.newReader()
                        .topic(topicName)
                        .startMessageId(MessageId.earliest)
                        .create()

    reader.withCloseable {
        while (reader.hasMessageAvailable()) {
            def msg = reader.readNext()
            def data = new String(msg.data, UTF_8)

            println data
        }
    }
}