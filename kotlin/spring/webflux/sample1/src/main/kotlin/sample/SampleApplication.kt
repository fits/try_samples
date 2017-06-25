package sample

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.reactive.config.EnableWebFlux

@EnableWebFlux
@SpringBootApplication
class SampleApplication

fun main(args: Array<String>) {
    SpringApplication.run(SampleApplication::class.java, *args)
}
