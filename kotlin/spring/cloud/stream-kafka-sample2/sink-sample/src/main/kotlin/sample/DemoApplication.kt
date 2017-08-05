package sample

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink

@SpringBootApplication
@EnableBinding(Sink::class)
class DemoApplication {
    @StreamListener(Sink.INPUT)
    fun receiveData(data: String) {
        println("*** receive: ${data}")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}
