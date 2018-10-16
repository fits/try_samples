package mongodb.sample.controller;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.Success;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import mongodb.sample.data.Stock;

import static com.mongodb.client.model.Filters.eq;

@Controller("/stocks")
public class StockController {
    @Property(name = "stocks.db")
    private String db;
    @Property(name = "stocks.collection")
    private String collection;

    private MongoClient repo;

    public StockController(MongoClient mongo) {
        repo = mongo;
    }

    @Get("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Maybe<Stock> find(String id) {
        return Flowable.fromPublisher(
                getCollection().find(eq("_id", id)).limit(1)
        ).firstElement();
    }

    @Post
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Single<Success> store(@Body Stock stock) {
        return Flowable.fromPublisher(
                getCollection().insertOne(stock)
        ).firstOrError();
    }

    private MongoCollection<Stock> getCollection() {
        return repo.getDatabase(db).getCollection(collection, Stock.class);
    }
}
