package stock.reserve.redis.repository;

import io.lettuce.core.RedisClient;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import stock.reserve.model.Reservation;
import stock.reserve.model.Stock;
import stock.reserve.repository.StockRepository;

import javax.inject.Singleton;
import java.time.Duration;
import java.util.UUID;

@Singleton
public class RedisStockRepository implements StockRepository {
    private static final String KEY_PREFIX_STOCK = "stock-";
    private static final String KEY_PREFIX_RESERVE = "stock-reserve-";

    private static final int RETRY_TIMES = 10;
    private static final Duration RETRY_FIRST_BACKOFF = Duration.ofMillis(30);
    private static final Duration RETRY_MAX_BACKOFF = Duration.ofMillis(200);

    private final RedisClient client;

    public RedisStockRepository(RedisClient client) {
        this.client = client;
    }

    @Override
    public Publisher<Stock> create(String id) {

        return connect().setnx(toStockKey(id), "0")
                .filter(s -> s)
                .map(s -> new Stock(id, 0, 0));
    }

    @Override
    public Publisher<Stock> update(String id, int newQuantity) {
        var key = toStockKey(id);

        var redis = connect();

        return redis.get(key)
                .flatMap(q -> redis.set(key, Integer.toString(newQuantity)))
                .flatMap(s -> findStock(redis, id));
    }

    @Override
    public Publisher<Stock> find(String id) {
        return findStock(connect(), id);
    }

    @Override
    public Publisher<Reservation> reserve(String id, int quantity) {
        var rsvKey = toReserveKey(id);
        var rsvId = UUID.randomUUID().toString();

        var redis = connect();

        return redis.watch(rsvKey)
                .flatMap(s -> findStock(redis, id))
                .filter(st -> st.getQuantity() - st.getReserved() >= quantity)
                .flatMap(st -> {
                    MonoProcessor<TransactionResult> proc = MonoProcessor.create();

                    redis.multi().subscribe(s -> {
                        redis.lpush(rsvKey, repeat(rsvId, quantity)).subscribe();
                        redis.exec().subscribe(proc);
                    });

                    return proc;
                })
                .flatMap(tr ->
                        tr.size() > 0 ?
                                Mono.just(tr) : Mono.error(new TransactionFailedException())
                )
                .retryBackoff(RETRY_TIMES, RETRY_FIRST_BACKOFF, RETRY_MAX_BACKOFF)
                .map(tr -> new Reservation(rsvId, quantity));
    }

    @Override
    public Publisher<Reservation> releaseReservation(String id, String rsvId) {

        return Mono.from(cancelReservation(id, rsvId))
                .flatMap(r ->
                        connect().decrby(toStockKey(id), r.getQuantity())
                                .retry(RETRY_TIMES)
                                .map(d -> r)
                );
    }

    @Override
    public Publisher<Reservation> cancelReservation(String id, String rsvId) {

        return connect().lrem(toReserveKey(id), 0, rsvId)
                .filter(r -> r > 0)
                .map(r -> new Reservation(rsvId, r.intValue()));
    }

    private Mono<Stock> findStock(RedisReactiveCommands<String, String> redis, String id) {
        var key = toStockKey(id);
        var rsvKey = toReserveKey(id);

        return redis.get(key)
                .flatMap(q ->
                        redis.llen(rsvKey)
                                .map(r -> new Stock(id, Integer.parseInt(q), r.intValue()))
                );
    }

    private RedisReactiveCommands<String, String> connect() {
        return client.connect().reactive();
    }

    private static String toStockKey(String id) {
        return KEY_PREFIX_STOCK + id;
    }

    private static String toReserveKey(String id) {
        return KEY_PREFIX_RESERVE + id;
    }

    private static String[] repeat(String s, int size) {
        String[] res = new String[size];

        for (int i = 0; i < res.length; i++) {
            res[i] = s;
        }

        return res;
    }

    private class TransactionFailedException extends Exception {
    }
}
