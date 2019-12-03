
import org.apache.pulsar.client.api.PulsarClient

fun main() {
    val pulsarUrl = "pulsar://localhost:6650"
    val topicName = "sample"

    val client = PulsarClient.builder().serviceUrl(pulsarUrl).build()

    client.use {
        val producer = client.newProducer().topic(topicName).create()

        producer.use {
            val msg = "sample1-${System.currentTimeMillis()}"

            val id = producer.send(msg.toByteArray(Charsets.UTF_8))
            println("message id: $id")

            val msg2 = "sample2-${System.currentTimeMillis()}"

            producer.sendAsync(msg2.toByteArray(Charsets.UTF_8))
                    .thenAccept {
                        println("async message id: $it")
                    }.exceptionally {
                        it.printStackTrace()
                        null
                    }.get()
        }
    }
}