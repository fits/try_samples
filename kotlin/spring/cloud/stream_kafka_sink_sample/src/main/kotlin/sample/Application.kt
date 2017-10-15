package sample

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.messaging.Message

@SpringBootApplication
@EnableBinding(Sink::class)
class Application {
    @StreamListener(Sink.INPUT)
    fun handle(msg: Message<ByteArray>) {
        val payload = msg.payload.toString(Charsets.UTF_8)
        println("*** received: ${payload}")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
