package sample

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class DiscoveryApplication

fun main(args: Array<String>) {
    SpringApplication.run(DiscoveryApplication::class.java, *args)
}
