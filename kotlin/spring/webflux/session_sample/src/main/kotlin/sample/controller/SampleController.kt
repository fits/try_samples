package sample.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono

@RestController
class SampleController {
    @GetMapping("/set/{value}")
    fun set(@PathVariable("value") value: String, session: WebSession): Mono<Data> {
        session.attributes["value"] = value
        return Mono.just(Data(value))
    }

    @GetMapping("/get")
    fun get(session: WebSession): Mono<Data> {
        val value = session.attributes["value"].toString()
        return session.invalidate().thenReturn(Data(value))
    }
}

data class Data(val value: String)