package sample.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sample.data.Data
import sample.repository.DataRepository

@RestController
class SampleController {
    @Autowired
    lateinit var dataRepository: DataRepository

    @GetMapping("/data/{id}")
    fun getData(@PathVariable("id") id: String) = dataRepository.findData(id)

    @PutMapping("/data")
    fun putData(@RequestBody data: Data) = dataRepository.saveData(data)
}