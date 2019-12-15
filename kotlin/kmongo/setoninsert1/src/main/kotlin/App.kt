import com.mongodb.ConnectionString
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*
import java.time.OffsetDateTime
import kotlin.reflect.full.memberProperties

typealias SampleDate = OffsetDateTime

data class Data(val code: String, val name: String, val revision: Long,
                val date: SampleDate = SampleDate.now())

fun main() {
    val conStr = "mongodb://localhost"
    val dbName = "sample"

    KMongo.createClient(ConnectionString(conStr)).use { client ->
        val col = client.getDatabase(dbName).getCollection<Data>()

        val d = Data("a1", "item-1",1)

        val res = col.findOneAndUpdate(
                and(
                        Data::code eq d.code,
                        Data::revision eq d.revision
                ),
                combine(
                        setOnInsert(Data::code, d.code),
                        setOnInsert(Data::name, d.name),
                        setOnInsert(Data::revision, d.revision),
                        setOnInsert(Data::date, d.date)
                ),
                findOneAndUpdateUpsert().returnDocument(ReturnDocument.AFTER)
        )

        println(res)

        val d2 = Data("b2", "item-2",1)

        val res2 = col.findOneAndUpdate(
                and(
                        Data::code eq d2.code,
                        Data::revision eq d2.revision
                ),
                combine(
                        Data::class.memberProperties.map {
                            setOnInsert(it, it.get(d2))
                        }
                ),
                findOneAndUpdateUpsert()
        )

        println(res2)
    }
}