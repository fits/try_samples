package sample.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class SampleController {
    @RequestMapping("/")
    fun home() = Mono.just("home")

    @RequestMapping(value = "/data/{name}", 
                    produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun dataList(@PathVariable("name") name: String) = Flux.fromArray(arrayOf(
        Data("${name}-1", 10),
        Data("${name}-2", 20)
    ))

    data class Data(val name: String, val value: Int)
}