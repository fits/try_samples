package sample

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController {
    @GetMapping("/")
    fun home() = "home page"

    @GetMapping("/data/{id}")
    fun data(@PathVariable("id") id: String) = "data-${id}"
}