package sample

import com.mongodb.ConnectionString
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
class DemoApplication {
    @Value("\${spring.data.mongodb.uri:mongodb://localhost/sample}")
    lateinit var mongoUri: String

    @Bean
    fun mongoFactory() = SimpleReactiveMongoDatabaseFactory(ConnectionString(mongoUri))

    @Bean
    fun reactiveMongoTemplate() = ReactiveMongoTemplate(mongoFactory())
}

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}
