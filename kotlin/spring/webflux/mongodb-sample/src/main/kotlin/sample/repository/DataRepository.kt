package sample.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Repository
import sample.data.Data

@Repository
class DataRepository {
    @Autowired
    lateinit var template: ReactiveMongoTemplate

    fun findData(id: String) = template.findById(id, Data::class.java)
    fun saveData(data: Data) = template.insert(data)
}