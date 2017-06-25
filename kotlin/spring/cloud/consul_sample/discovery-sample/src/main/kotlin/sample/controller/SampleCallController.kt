package sample.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
@EnableDiscoveryClient(autoRegister = false)
class SampleCallController {
    companion object {
        const val TARGET_SERVICE_ID = "sample"
    }

    @Autowired
    lateinit var discoveryClient: DiscoveryClient

    val restTemplate = RestTemplate()

    @RequestMapping("/")
    fun callSample() = serviceUrl()?.let {
        val res = restTemplate.getForObject(it, String::class.java)
        "result: ${res}"
    }

    fun serviceUrl() = discoveryClient.getInstances(TARGET_SERVICE_ID)
            .firstOrNull()?.let { it.uri }
}