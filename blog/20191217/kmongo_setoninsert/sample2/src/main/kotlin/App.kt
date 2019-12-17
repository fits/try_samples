import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.mongodb.ConnectionString
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*
import java.time.OffsetDateTime

typealias EventDate = OffsetDateTime

interface EventDetail

data class Created(val value: Int) : EventDetail
data class Updated(val oldValue: Int, val newValue: Int) : EventDetail

data class Event(val targetId: String, val revision: Long,
                 @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) val detail: EventDetail,
                 val date: EventDate = EventDate.now())

fun main() {
    val conStr = "mongodb://localhost"
    val dbName = "sample2"

    KMongo.createClient(ConnectionString(conStr)).use { client ->
        val col = client.getDatabase(dbName).getCollection<Event>()

        val d1 = Event("a1", 1, Created(1))

        val res1 = col.findOneAndUpdate(
                and(
                        Event::targetId eq d1.targetId,
                        Event::revision eq d1.revision
                ),
                "{${MongoOperator.setOnInsert}: ${d1.json}}".bson,
                findOneAndUpdateUpsert().returnDocument(ReturnDocument.AFTER)
        )

        println(res1)

        val d2 = Event("a1", 2, Updated(1, 2))

        val res2 = col.findOneAndUpdate(
                and(
                        Event::targetId eq d2.targetId,
                        Event::revision eq d2.revision
                ),
                "{${MongoOperator.setOnInsert}: ${d2.json}}".bson,
                findOneAndUpdateUpsert().returnDocument(ReturnDocument.AFTER)
        )

        println(res2)
    }
}