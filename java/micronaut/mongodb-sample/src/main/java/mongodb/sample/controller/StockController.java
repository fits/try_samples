package mongodb.sample.controller;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.annotation.*;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

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
    public Maybe<Stock> find(String id) {
        return Flowable.fromPublisher(
                getCollection().find(eq("_id", id)).first()
        ).firstElement();
    }

    @Post
    public Maybe<Stock> store(@Body Stock stock) {
        return Flowable.fromPublisher(
                getCollection().insertOne(stock)
        ).map(s -> stock).firstElement();
    }

    private MongoCollection<Stock> getCollection() {
        return repo.getDatabase(db).getCollection(collection, Stock.class);
    }
}
