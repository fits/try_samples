import com.mongodb.ConnectionString
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*
import java.time.OffsetDateTime
import kotlin.reflect.full.memberProperties

typealias EventDate = OffsetDateTime

data class Event(val targetId: String, val revision: Long,
                 val date: EventDate = EventDate.now())

fun main() {
    val conStr = "mongodb://localhost"
    val dbName = "sample"

    KMongo.createClient(ConnectionString(conStr)).use { client ->
        val col = client.getDatabase(dbName).getCollection<Event>()

        val d1 = Event("a1", 1)

        val res = col.findOneAndUpdate(
                and(
                        Event::targetId eq d1.targetId,
                        Event::revision eq d1.revision
                ),
                combine(
                        setOnInsert(Event::targetId, d1.targetId),
                        setOnInsert(Event::revision, d1.revision),
                        setOnInsert(Event::date, d1.date)
                ),
                findOneAndUpdateUpsert()
        )

        println(res)

        val d2 = Event("b1", 1)

        val res2 = col.findOneAndUpdate(
                and(
                        Event::targetId eq d2.targetId,
                        Event::revision eq d2.revision
                ),
                combine(
                        Event::class.memberProperties.map {
                            setOnInsert(it, it.get(d2))
                        }
                ),
                findOneAndUpdateUpsert().returnDocument(ReturnDocument.AFTER)
        )

        println(res2)
    }
}