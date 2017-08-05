package sample

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
@EnableBinding(Source::class)
class DemoApplication {
    @Autowired
    lateinit var source: Source

    @PutMapping("/data")
    fun putData(@RequestBody data: Data) {
        source.output().send(MessageBuilder.withPayload(data).build())
    }

    @StreamListener(Source.OUTPUT)
    fun receiveData(data: Data) {
        println("*** receive data: ${data}")
    }
}

data class Data(val id: String, val value: Int)

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}
