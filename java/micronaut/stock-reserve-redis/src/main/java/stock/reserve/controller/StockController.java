package stock.reserve.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.annotation.*;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import stock.reserve.model.Reservation;
import stock.reserve.model.Stock;
import stock.reserve.repository.StockRepository;

@Controller("/stocks")
public class StockController {
    private final StockRepository repository;

    public StockController(StockRepository repository) {
        this.repository = repository;
    }

    @Post
    public Maybe<Stock> create(
            @JsonProperty("id") String id,
            @JsonProperty("quantity") int quantity) {

        return Flowable.fromPublisher(repository.create(id))
                .flatMap(st -> repository.update(st.getId(), quantity))
                .firstElement();
    }

    @Put("{id}")
    public Maybe<Stock> update(String id, @JsonProperty("quantity") int quantity) {
        return Flowable.fromPublisher(repository.update(id, quantity)).firstElement();
    }

    @Get("{id}")
    public Maybe<Stock> find(String id) {
        return Flowable.fromPublisher(repository.find(id)).firstElement();
    }

    @Post("{id}/reserve")
    public Maybe<Reservation> reserve(String id, @JsonProperty("quantity") int quantity) {
        return Flowable.fromPublisher(repository.reserve(id, quantity)).firstElement();
    }

    @Delete("{id}/reserve/{rsvId}")
    public Maybe<Reservation> releaseReservation(String id, String rsvId,
                @QueryValue(value = "complete", defaultValue = "false") boolean complete) {

        var proc = complete ?
                repository.releaseReservation(id, rsvId) :
                repository.cancelReservation(id, rsvId);

        return Flowable.fromPublisher(proc).firstElement();
    }
}
