
import org.apache.pulsar.client.api.PulsarClient

fun main() {
    val pulsarUrl = "pulsar://localhost:6650"
    val topicName = "sample"

    val client = PulsarClient.builder().serviceUrl(pulsarUrl).build()

    client.use {
        val consumer = client.newConsumer()
                .topic(topicName)
                .subscriptionName("sb1")
                .subscribe()

        consumer.use {
            while (true) {
                val msg = consumer.receive()

                try {
                    val data = msg.data.toString(Charsets.UTF_8)
                    println("receive: $data")
                    consumer.acknowledge(msg)
                } catch (e: Exception) {
                    consumer.negativeAcknowledge(msg)
                }
            }
        }
    }
}