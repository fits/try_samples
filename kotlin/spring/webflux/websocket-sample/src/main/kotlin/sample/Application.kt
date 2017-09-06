package sample

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import sample.controller.SampleHandler

@SpringBootApplication
@EnableWebFlux
@Configuration
class Application : WebFluxConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
        registry?.let {
            it.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:static/")
        }
    }

    @Bean
    fun webSocketMapping(): HandlerMapping {
        val mapping = SimpleUrlHandlerMapping()

        mapping.urlMap = mapOf("/sample" to SampleHandler())

        return mapping
    }

    @Bean
    fun handlerAdapter() = WebSocketHandlerAdapter()
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
