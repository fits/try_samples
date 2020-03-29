
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.mongodb.javadsl.MongoSource
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.Document

fun main() {
    MongoClients.create().use { mongo ->
        val system = ActorSystem.create("sample")
        val mat = Materializer.createMaterializer(system)

        val db = mongo.getDatabase("sample")

        val col = db.getCollection("data", Document::class.java)
                    .withDocumentClass(Document::class.java)

        val d1 = Document.parse("""{"type": "date", "value": ${System.currentTimeMillis()}}""")

        val proc = MongoSource.create(col.insertOne(d1)).flatMapConcat {
            MongoSource.create(col.find())
        }

        proc.runForeach({ println(it) }, mat).toCompletableFuture().get()

        system.terminate()
    }
}
