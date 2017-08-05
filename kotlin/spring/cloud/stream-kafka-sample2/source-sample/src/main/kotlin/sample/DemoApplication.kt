package sample

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableBinding(Source::class)
@RestController
class DemoApplication {
    @Autowired
    lateinit var source: Source

    @PutMapping("/data")
    fun putData(@RequestBody data: String) {
        println("*** putData: ${data}")
        source.output().send(MessageBuilder.withPayload(data).build())
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}
