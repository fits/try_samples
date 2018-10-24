package stock.reserve.mongo.repository;

import com.mongodb.client.model.Projections;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.context.annotation.Property;
import io.reactivex.Flowable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import stock.reserve.model.Reservation;
import stock.reserve.model.Stock;
import stock.reserve.repository.StockRepository;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

@Singleton
public class MongoStockRepository implements StockRepository {

    private static final String FIELD_ID = "_id";
    private static final String FIELD_QUANTITY = "quantity";
    private static final String FIELD_RESERVED = "reserved";
    private static final String FIELD_RESERVATIONS = "reservations";
    private static final String FIELD_RSV_ID = "rsvId";

    private final MongoClient mongoClient;
    private final String dbName;
    private final String collectionName;

    public MongoStockRepository(MongoClient mongoClient,
                                @Property(name = "mongodb.db") String dbName,
                                @Property(name = "mongodb.collection") String collectionName) {
        this.mongoClient = mongoClient;
        this.dbName = dbName;
        this.collectionName = collectionName;
    }

    @Override
    public Publisher<Stock> create(String id) {
        var stock = new Stock(id, 0, 0);

        return Flowable.fromPublisher(stockCol().insertOne(stock))
                .map(s -> stock);
    }

    @Override
    public Publisher<Stock> update(String id, int newQuantity) {
        return Flowable.fromPublisher(
                stockCol().findOneAndUpdate(eqId(id), set(FIELD_QUANTITY, newQuantity))
        ).map(s -> new Stock(s.getId(), newQuantity, s.getReserved()));
    }

    @Override
    public Publisher<Stock> find(String id) {
        return stockCol().find(eqId(id));
    }

    @Override
    public Publisher<Reservation> reserve(String id, int quantity) {
        var rsv = new Reservation(UUID.randomUUID().toString(), quantity);

        var query = and(
            eqId(id),
            expr(new Document("$gte", List.of(
                    new Document("$subtract", List.of("$" + FIELD_QUANTITY, "$" + FIELD_RESERVED)),
                    rsv.getQuantity()
            )))
        );

        var command = combine(
                inc(FIELD_RESERVED, rsv.getQuantity()),
                push(FIELD_RESERVATIONS, rsv)
        );

        return Flowable.fromPublisher(stockCol().findOneAndUpdate(query, command))
                .map(st -> rsv);
    }

    @Override
    public Publisher<Reservation> releaseReservation(String id, String rsvId) {
        return removeReservation(id, rsvId)
                .flatMap(rsv ->
                        updateDiff(id, -rsv.getQuantity())
                                .map(st -> rsv)
                );
    }

    @Override
    public Publisher<Reservation> cancelReservation(String id, String rsvId) {
        return removeReservation(id, rsvId);
    }

    private Flowable<Reservation> removeReservation(String id, String rsvId) {
        var query = and(eqId(id), eq(FIELD_RESERVATIONS + "." + FIELD_RSV_ID, rsvId));
        var projection = Projections.elemMatch(FIELD_RESERVATIONS, eq(FIELD_RSV_ID, rsvId));

        Function<Integer, Bson> genCommand = qty ->
                combine(
                        inc(FIELD_RESERVED, -qty),
                        pull(FIELD_RESERVATIONS, new Document(FIELD_RSV_ID, rsvId))
                );

        var col = collection(Document.class);

        return Flowable.fromPublisher(col.find(query).projection(projection))
                .map(d -> (Document)d.get(FIELD_RESERVATIONS, List.class).get(0))
                .map(this::toReservation)
                .flatMap(rsv ->
                        Flowable.fromPublisher(
                                col.findOneAndUpdate(query, genCommand.apply(rsv.getQuantity()))
                        ).map(s -> rsv)
                );
    }

    private Flowable<Stock> updateDiff(String id, int quantityDiff) {
        return Flowable.fromPublisher(
                stockCol().findOneAndUpdate(eqId(id), inc(FIELD_QUANTITY, quantityDiff))
        );
    }

    private Reservation toReservation(Document doc) {
        return new Reservation(
                doc.getString(FIELD_RSV_ID),
                doc.getInteger(FIELD_QUANTITY)
        );
    }

    private Bson eqId(String id) {
        return eq(FIELD_ID, id);
    }

    private MongoCollection<Stock> stockCol() {
        return collection(Stock.class);
    }

    private <T> MongoCollection<T> collection(Class<T> cls) {
        return mongoClient.getDatabase(dbName).getCollection(collectionName, cls);
    }
}
