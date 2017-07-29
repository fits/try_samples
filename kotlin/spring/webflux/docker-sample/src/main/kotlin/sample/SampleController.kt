package sample

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

data class Data(val id: String, val value: Int)

@RestController
class SampleController {
    @GetMapping("/data/{id}")
    fun getData(@PathVariable("id") id: String) = Mono.just(Data(id, 1))
}