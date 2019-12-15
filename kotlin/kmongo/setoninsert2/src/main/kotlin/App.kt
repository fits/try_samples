import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.mongodb.ConnectionString
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*
import java.time.OffsetDateTime

typealias EventDate = OffsetDateTime

interface EventStatus
data class Opened(val note: String) : EventStatus

data class Event(val code: String, val name: String, val revision: Long,
                 @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) val status: EventStatus,
                 val date: EventDate = EventDate.now())

fun main() {
    val conStr = "mongodb://localhost"
    val dbName = "sample"

    KMongo.createClient(ConnectionString(conStr)).use { client ->
        val col = client.getDatabase(dbName).getCollection<Event>()

        val d = Event("a1", "item-1",1, Opened("sample"))

        val res = col.findOneAndUpdate(
                and(
                        Event::code eq d.code,
                        Event::revision eq d.revision
                ),
                "{${MongoOperator.setOnInsert}: ${d.json}}".bson,
                findOneAndUpdateUpsert().returnDocument(ReturnDocument.AFTER)
        )

        println(res)
    }
}