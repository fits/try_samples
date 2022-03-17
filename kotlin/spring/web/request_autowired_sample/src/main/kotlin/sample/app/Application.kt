package sample.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@RestController
class Controller {
    @Autowired
    lateinit var service: SampleService

    @RequestMapping("/")
    fun root(): Info {
        println(service)
        return service.call()
    }
}

@Service
class SampleService {
    @Autowired
    lateinit var request: HttpServletRequest

    fun call(): Info {
        println(request)
        return Info(request.serverName)
    }
}

data class Info(val name: String)