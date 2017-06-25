package sample.controller

import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@EnableDiscoveryClient
class SampleController {
    @RequestMapping("/")
    fun sample() = "sample"
}