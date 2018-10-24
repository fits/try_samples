package stock.reserve.repository;

import org.reactivestreams.Publisher;
import stock.reserve.model.Reservation;
import stock.reserve.model.Stock;

public interface StockRepository {
    Publisher<Stock> create(String id);
    Publisher<Stock> update(String id, int newQuantity);
    Publisher<Stock> find(String id);

    Publisher<Reservation> reserve(String id, int quantity);
    Publisher<Reservation> releaseReservation(String id, String rsvId);
    Publisher<Reservation> cancelReservation(String id, String rsvId);
}
