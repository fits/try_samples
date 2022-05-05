package com.example.repository;

import com.example.model.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveCrudRepository<Item, String> {
    @Query("SELECT * FROM item WHERE price >= :price")
    Flux<Item> findBy(long price);
}
