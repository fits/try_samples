package sample;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;

public class App {
    private final static String DB_NAME = "sample";
    private final static String COLLECTION_NAME = "tasks";

    public static void main(String... args) {
        try (var mongo = createClient()) {
            var db = mongo.getDatabase(DB_NAME);
            var col = db.getCollection(COLLECTION_NAME, Task.class);

            var res = Flux.concat(
                    col.insertOne(new Task("sample1")),
                    col.insertOne(new Task("sample2", Status.Done)),
                    col.insertOne(new Task("sample3")),
                    col.find(eq("status", Status.Todo))
            );

            for (var r : res.toIterable()) {
                System.out.println(r);
            }
        }
    }

    private static MongoClient createClient() {
        var pojoCodecRegistry = CodecRegistries.fromProviders(
                PojoCodecProvider.builder()
                        .automatic(true)
                        .build()
        );

        var codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry
        );

        var settings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .build();

        return MongoClients.create(settings);
    }

    public static class Task {
        private ObjectId id;
        private String subject;
        private Status status;

        public Task() {
        }

        public Task(String subject) {
            setSubject(subject);
            setStatus(Status.Todo);
        }

        public Task(String subject, Status status) {
            setSubject(subject);
            setStatus(status);
        }

        public ObjectId getId() {
            return id;
        }

        public void setId(ObjectId id) {
            this.id = id;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "Task(id=" + id + ", subject=" + subject + ", status=" + status + ")";
        }
    }

    public enum Status { Todo, Done }
}
