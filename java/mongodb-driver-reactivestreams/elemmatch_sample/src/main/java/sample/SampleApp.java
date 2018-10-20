package sample;

import com.mongodb.client.model.Projections;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class SampleApp {
    private final static String DB_NAME = "sample";
    private final static String COLLECTION_NAME = "stocks";

    public static void main(String... args) {
        var qty = 10;
        var trgRsvId = "rsv2";

        try (var mongo = MongoClients.create()) {
            var col = mongo.getDatabase(DB_NAME).getCollection(COLLECTION_NAME);

            var proc = createStock(col, qty)
                        .flatMap(id -> findReservation(col, id, trgRsvId));

            System.out.println(proc.blockOptional());
        }
    }

    private static Mono<Document> findReservation(MongoCollection<Document> col,
                                                  String id, String rsvId) {

        var cond = and(
                eq("_id", id),
                eq("reservations.rsvId", rsvId)
        );

        var projection =
                Projections.elemMatch("reservations", eq("rsvId", rsvId));

        return Mono.from(
                col.find(cond).projection(projection).first()
        ).map(d -> {
            System.out.println(d);
            return (Document)d.get("reservations", List.class).get(0);
        });
	}

    private static Mono<String> createStock(MongoCollection<Document> col, int quantity) {
        var rsvs = List.of(
                createReservation("rsv1", 1),
                createReservation("rsv2", 2),
                createReservation("rsv3", 3)
        );

        var id = UUID.randomUUID().toString();

        var stock = new Document(Map.of(
                "_id", id,
                "quantity", quantity,
                "reservations", rsvs
        ));

        return Mono.from(
        		col.insertOne(stock)
		).map(r -> id);
    }

    private static Document createReservation(String rsvId, int quantity) {
        return new Document(Map.of(
                "rsvId", rsvId,
                "quantity", quantity
        ));
    }
}
